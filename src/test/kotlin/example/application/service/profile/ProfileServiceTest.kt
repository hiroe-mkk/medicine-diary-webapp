package example.application.service.profile

import com.ninjasquad.springmockk.*
import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.infrastructure.storage.shared.objectstrage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import org.assertj.core.api.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.util.*

@MyBatisRepositoryTest
internal class ProfileServiceTest(@Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    private val usernameChangeValidationService: UsernameChangeValidationService = UsernameChangeValidationService(profileRepository)
    private val profileService: ProfileService = ProfileService(profileRepository, usernameChangeValidationService)

    private lateinit var userSession: UserSession
    private lateinit var requesterProfile: Profile

    @BeforeEach
    internal fun setUp() {
        requesterProfile = testAccountInserter.insertAccountAndProfile().second
        userSession = UserSessionFactory.create(requesterProfile.accountId)
    }

    @Test
    @DisplayName("プロフィールを取得する")
    fun getProfile() {
        //when:
        val actual = profileService.findProfile(userSession)

        //then:
        val expected = ProfileDto(requesterProfile.username, requesterProfile.profileImageURL)
        assertThat(actual).isEqualTo(expected)
    }

    @Nested
    inner class ChangeUsernameTest {
        @Test
        @DisplayName("ユーザー名を変更する")
        fun changeUsername() {
            //given:
            val command = UsernameEditCommand("newTestUsername")

            //when:
            profileService.changeUsername(command, userSession)

            //then:
            val actual = profileRepository.findByAccountId(userSession.accountId)
            val expected = Profile.reconstruct(userSession.accountId, Username("newTestUsername"), null)
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("既に同じユーザー名が登録されていた場合、ユーザー名の変更に失敗する")
        fun sameUsernameExists_changingUsernameFails() {
            //given:
            val duplicateUsername = Username("newTestUsername")
            testAccountInserter.insertAccountAndProfile(username = duplicateUsername)
            val command = UsernameEditCommand(duplicateUsername.value)

            //when;
            val target: () -> Unit = { profileService.changeUsername(command, userSession) }

            //then:
            val usernameChangeException = assertThrows<UsernameChangeException>(target)
            assertThat(usernameChangeException.username).isEqualTo(duplicateUsername)
        }
    }
}
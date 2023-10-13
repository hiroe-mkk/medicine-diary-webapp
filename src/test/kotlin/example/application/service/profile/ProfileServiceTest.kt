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
    private val profileService: ProfileService = ProfileService(profileRepository)

    private lateinit var userSession: UserSession
    private lateinit var profile: Profile

    @BeforeEach
    internal fun setUp() {
        val (createdAccount, createdProfile) = testAccountInserter.insertAccountAndProfile()
        profile = createdProfile
        userSession = UserSessionFactory.create(createdAccount.id)
    }

    @Nested
    inner class GetProfileTest {
        @Test
        @DisplayName("プロフィールを取得する")
        fun getProfile() {
            //when:
            val actual = profileService.findProfile(userSession)

            //then:
            val expected = ProfileDto(profile.username, profile.profileImageURL)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("プロフィールが見つからなかった場合、プロフィールの取得に失敗する")
        fun profileNotFound_gettingProfileFails() {
            //given:
            val badUserSession = UserSessionFactory.create(AccountId("NonexistentId"))

            //when:
            val target: () -> Unit = { profileService.findProfile(badUserSession) }

            //then:
            val profileNotFoundException = assertThrows<ProfileNotFoundException>(target)
            assertThat(profileNotFoundException.accountId).isEqualTo(badUserSession.accountId)
        }
    }

    @Nested
    inner class ChangeUsernameTest {
        private val command = UsernameEditCommand("newTestUsername")

        @Test
        @DisplayName("ユーザー名を変更する")
        fun changeUsername() {
            //when:
            profileService.changeUsername(command, userSession)

            //then: 変更されたプロフィールが保存されている
            val actual = profileRepository.findByAccountId(userSession.accountId)
            val expected = Profile.reconstruct(userSession.accountId, Username("newTestUsername"), null)
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("プロフィールが見つからなかった場合、ユーザー名の変更に失敗する")
        fun profileNotFound_changingUsernameFails() {
            //given:
            profileRepository.delete(profile.accountId)

            //when:
            val target: () -> Unit = { profileService.changeUsername(command, userSession) }

            //then:
            val profileNotFoundException = assertThrows<ProfileNotFoundException>(target)
            assertThat(profileNotFoundException.accountId).isEqualTo(userSession.accountId)
        }
    }
}
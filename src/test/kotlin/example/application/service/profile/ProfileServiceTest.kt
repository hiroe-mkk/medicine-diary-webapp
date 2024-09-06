package example.application.service.profile

import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class ProfileServiceTest(@Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    private val profileService: ProfileService = ProfileService(profileRepository)

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
}

package example.application.service.profile

import example.application.service.account.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.profile.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class ProfileServiceTest(@Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    private val profileService: ProfileService = ProfileService(profileRepository)

    private lateinit var userSession: UserSession
    private lateinit var profile: Profile

    @BeforeEach
    internal fun setUp() {
        val (createdAccount, createdProfile) = testAccountInserter.createAndInsert()
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
            val expected = ProfileDto(profile.username)
            assertThat(actual).isEqualTo(expected)
        }

        @Test
        @DisplayName("アカウントが見つからなかった場合、プロフィールの取得に失敗する")
        fun accountNotFound_profileAcquisitionFails() {
            //given:
            val badUserSession = UserSessionFactory.create(AccountId("NonexistentId"))

            //when:
            val target: () -> Unit = { profileService.findProfile(badUserSession) }

            //then:
            val accountNotFoundException = assertThrows<AccountNotFoundException>(target)
            assertThat(accountNotFoundException.accountId).isEqualTo(badUserSession.accountId)
        }
    }
}
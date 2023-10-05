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
import org.assertj.core.api.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.util.*

@MyBatisRepositoryTest
internal class ProfileServiceTest(@Autowired private val profileRepository: ProfileRepository,
                                  @Autowired private val accountRepository: AccountRepository,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    private val profileImageStorage: ProfileImageStorage = mockk(relaxed = true)
    private val profileService: ProfileService = ProfileService(profileRepository, profileImageStorage)
    private val accountService: AccountService = AccountService(accountRepository, profileRepository)

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
        @DisplayName("アカウントが見つからなかった場合、プロフィールの取得に失敗する")
        fun accountNotFound_gettingProfileFails() {
            //given:
            val badUserSession = UserSessionFactory.create(AccountId("NonexistentId"))

            //when:
            val target: () -> Unit = { profileService.findProfile(badUserSession) }

            //then:
            val accountNotFoundException = assertThrows<AccountNotFoundException>(target)
            assertThat(accountNotFoundException.accountId).isEqualTo(badUserSession.accountId)
        }
    }

    @Nested
    inner class ChangeUsernameTest {
        private val usernameChangeCommand = UsernameChangeCommand("newTestUsername")

        @Test
        @DisplayName("ユーザー名を変更する")
        fun changeUsername() {
            //when:
            profileService.changeUsername(usernameChangeCommand, userSession)

            //then: 変更されたプロフィールが保存されている
            val actual = profileRepository.findByAccountId(userSession.accountId)
            val expected = Profile.reconstruct(userSession.accountId, Username("newTestUsername"), null)
            assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("アカウントが削除済みの場合、ユーザー名の変更に失敗する")
        fun accountHasBeenDeleted_changingUsernameFails() {
            //given:
            accountService.deleteAccount(userSession)

            //when:
            val target: () -> Unit = { profileService.changeUsername(usernameChangeCommand, userSession) }

            //then:
            val accountNotFoundException = assertThrows<AccountNotFoundException>(target)
            assertThat(accountNotFoundException.accountId).isEqualTo(userSession.accountId)
        }
    }

    @Nested
    inner class ChangeProfileImageTest {
        private val profileImageChangeCommand = TestProfileImageFactory.createProfileImageChangeCommand()

        @BeforeEach
        internal fun setUp() {
            every {
                profileImageStorage.createURL()
            } returns ProfileImageURL("endpoint", "/profileimage/newProfileImage")
        }

        @Test
        @DisplayName("プロフィール画像が未設定の場合、プロフィール画像の変更に成功する")
        fun profileImageIsNull_changingProfileImageSucceeds() {
            //when:
            val newProfileImageURL = profileService.changeProfileImage(profileImageChangeCommand, userSession)

            //then:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            assertThat(profile.profileImageURL).isEqualTo(newProfileImageURL)
            verify(exactly = 0) { profileImageStorage.delete(any()) }
            verify(exactly = 1) { profileImageStorage.upload(any()) }
        }

        @Test
        @DisplayName("プロフィール画像が設定済みの場合、プロフィール画像の変更に成功する")
        fun profileImageIsNotNull_changingProfileImageSucceeds() {
            //given:
            val oldProfileImageURL = ProfileImageURL("endpoint", "/profileimage/oldProfileImage")
            profile.changeProfileImage(oldProfileImageURL)
            profileRepository.save(profile)

            //when:
            val newProfileImageURL = profileService.changeProfileImage(profileImageChangeCommand, userSession)

            //then:
            val profile = profileRepository.findByAccountId(userSession.accountId)!!
            assertThat(profile.profileImageURL).isEqualTo(newProfileImageURL)
            verify(exactly = 1) { profileImageStorage.upload(any()) }
            verify(exactly = 1) { profileImageStorage.delete(oldProfileImageURL) }
        }
    }
}
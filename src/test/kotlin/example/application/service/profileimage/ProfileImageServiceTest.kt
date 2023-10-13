package example.application.service.profileimage

import example.application.service.account.*
import example.application.service.profile.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisRepositoryTest
internal class ProfileImageServiceTest(@Autowired private val profileRepository: ProfileRepository,
                                       @Autowired private val testAccountInserter: TestAccountInserter) {
    @MockK(relaxed = true)
    private lateinit var profileImageStorage: ProfileImageStorage

    @InjectMockKs
    private lateinit var profileImageService: ProfileImageService

    private lateinit var userSession: UserSession
    private lateinit var profile: Profile

    private val command = TestImageFactory.createImageUploadCommand()

    @BeforeEach
    internal fun setUp() {
        val (account, createdProfile) = testAccountInserter.insertAccountAndProfile()
        profile = createdProfile
        userSession = UserSessionFactory.create(account.id)
        every {
            profileImageStorage.createURL()
        } returns ProfileImageURL("endpoint", "/profileimage/newProfileImage")
    }

    @Test
    @DisplayName("プロフィール画像が未設定の場合、プロフィール画像の変更に成功する")
    fun profileImageIsNull_changingProfileImageSucceeds() {
        //when:
        val newProfileImageURL = profileImageService.changeProfileImage(command, userSession)

        //then:
        val profile = profileRepository.findByAccountId(userSession.accountId)!!
        Assertions.assertThat(profile.profileImageURL).isEqualTo(newProfileImageURL)
        verify(exactly = 0) { profileImageStorage.delete(any()) }
        verify(exactly = 1) { profileImageStorage.upload(newProfileImageURL, any()) }
    }

    @Test
    @DisplayName("プロフィール画像が設定済みの場合、プロフィール画像の変更に成功する")
    fun profileImageIsNotNull_changingProfileImageSucceeds() {
        //given:
        val oldProfileImageURL = ProfileImageURL("endpoint", "/profileimage/oldProfileImage")
        profile.changeProfileImage(oldProfileImageURL)
        profileRepository.save(profile)

        //when:
        val newProfileImageURL = profileImageService.changeProfileImage(command, userSession)

        //then:
        val profile = profileRepository.findByAccountId(userSession.accountId)!!
        Assertions.assertThat(profile.profileImageURL).isEqualTo(newProfileImageURL)
        verify(exactly = 1) { profileImageStorage.upload(newProfileImageURL, any()) }
        verify(exactly = 1) { profileImageStorage.delete(oldProfileImageURL) }
    }

    @Test
    @DisplayName("プロフィールが見つからなかった場合、プロフィール画像の変更に失敗する")
    fun profileNotFound_changingProfileImageFails() {
        //given:
        profileRepository.delete(profile.accountId)

        //when:
        val target: () -> Unit = { profileImageService.changeProfileImage(command, userSession) }

        //then:
        val profileNotFoundException = assertThrows<ProfileNotFoundException>(target)
        Assertions.assertThat(profileNotFoundException.accountId).isEqualTo(userSession.accountId)
    }
}
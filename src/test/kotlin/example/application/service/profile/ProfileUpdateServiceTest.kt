package example.application.service.profile

import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.infrastructure.objectstorage.shared.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class ProfileUpdateServiceTest(@Autowired private val profileRepository: ProfileRepository,
                               @Autowired private val profileImageStorage: ProfileImageStorage,
                               @Autowired private val objectStorageClient: ObjectStorageClient,
                               @Autowired private val testAccountInserter: TestAccountInserter) {
    private val profileImageService = ProfileUpdateService(profileRepository, profileImageStorage)

    @BeforeEach
    fun setUp() {
        clearMocks(objectStorageClient)
    }

    @Test
    @DisplayName("ユーザー名を変更する")
    fun changeUsername() {
        //given:
        val (account, _) = testAccountInserter.insertAccountAndProfile()
        val userSession = UserSessionFactory.create(account.id)
        val command = UsernameEditCommand("newTestUsername")

        //when:
        profileImageService.changeUsername(command, userSession)

        //then:
        val actual = profileRepository.findByAccountId(userSession.accountId)
        val expected = Profile.reconstruct(userSession.accountId, Username("newTestUsername"), null)
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
    }

    @Nested
    inner class ChangeProfileImage {
        private val command = TestImageFactory.createImageUploadCommand()

        @Test
        @DisplayName("プロフィール画像が設定されていない場合、プロフィール画像の変更に成功する")
        fun profileImageIsNotSet_changingProfileImageSucceeds() {
            //given:
            val requesterAccountId = testAccountInserter.insertAccountAndProfile(profileImageURL = null).first.id
            val userSession = UserSessionFactory.create(requesterAccountId)

            //when:
            val newProfileImageURL = profileImageService.changeProfileImage(command, userSession)

            //then:
            val foundProfile = profileRepository.findByAccountId(userSession.accountId)!!
            assertThat(foundProfile.profileImageURL).isEqualTo(newProfileImageURL)
            verify(exactly = 1) { objectStorageClient.put(newProfileImageURL, any()) }
        }

        @Test
        @DisplayName("プロフィール画像が設定されている場合、プロフィール画像の変更に成功する")
        fun profileImageIsSet_changingProfileImageSucceeds() {
            //given:
            val oldProfileImageURL = profileImageStorage.createURL()
            val requesterAccountId =
                    testAccountInserter.insertAccountAndProfile(profileImageURL = oldProfileImageURL).first.id
            val userSession = UserSessionFactory.create(requesterAccountId)

            //when:
            val newProfileImageURL = profileImageService.changeProfileImage(command, userSession)

            //then:
            val foundProfile = profileRepository.findByAccountId(userSession.accountId)!!
            assertThat(foundProfile.profileImageURL).isEqualTo(newProfileImageURL)
            verify(exactly = 1) { objectStorageClient.put(newProfileImageURL, any()) }
            verify(exactly = 1) { objectStorageClient.remove(oldProfileImageURL) }
        }
    }

    @Test
    @DisplayName("プロフィール画像を削除する")
    fun deleteProfileImage() {
        //given:
        val profileImageURL = profileImageStorage.createURL()
        val requesterAccountId =
                testAccountInserter.insertAccountAndProfile(profileImageURL = profileImageURL).first.id
        val userSession = UserSessionFactory.create(requesterAccountId)

        //when:
        profileImageService.deleteProfileImage(userSession)

        //then:
        val foundProfile = profileRepository.findByAccountId(userSession.accountId)!!
        assertThat(foundProfile.profileImageURL).isNull()
        verify(exactly = 1) { objectStorageClient.remove(profileImageURL) }
    }
}

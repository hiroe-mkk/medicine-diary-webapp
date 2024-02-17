package example.application.service.profileimage

import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
internal class ProfileImageServiceTest(@Autowired private val profileRepository: ProfileRepository,
                                       @Autowired private val profileImageStorage: ProfileImageStorage,
                                       @Autowired private val testAccountInserter: TestAccountInserter) {
    private val profileImageService: ProfileImageService = ProfileImageService(profileRepository, profileImageStorage)

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
            verify(exactly = 1) { profileImageStorage.upload(newProfileImageURL, any()) }
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
            verify(exactly = 1) { profileImageStorage.upload(newProfileImageURL, any()) }
            verify(exactly = 1) { profileImageStorage.delete(oldProfileImageURL) }
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
        verify(exactly = 1) { profileImageStorage.delete(profileImageURL) }
    }
}
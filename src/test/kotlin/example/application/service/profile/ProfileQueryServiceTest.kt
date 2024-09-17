package example.application.service.profile

import example.domain.model.account.profile.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@DomainLayerTest
class ProfileQueryServiceTest(@Autowired private val profileRepository: ProfileRepository,
                              @Autowired private val testAccountInserter: TestAccountInserter) {
    private val profileService = ProfileQueryService(profileRepository)

    @Test
    @DisplayName("プロフィールを取得する")
    fun getProfile() {
        //given:
        val profile = testAccountInserter.insertAccountAndProfile().second
        val userSession = UserSessionFactory.create(profile.accountId)

        //when:
        val actual = profileService.getProfile(userSession)

        //then:
        assertThat(actual).isEqualTo(ProfileDto(profile.username,
                                                profile.profileImageURL))
    }
}

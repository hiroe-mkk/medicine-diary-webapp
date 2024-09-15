package example.presentation.shared.springframework.security.oauth2

import example.application.service.account.*
import example.domain.model.account.*
import org.springframework.security.oauth2.client.userinfo.*
import org.springframework.security.oauth2.core.user.*
import org.springframework.stereotype.*

@Component
class OAuth2UserAccountService(private val accountAuthenticationService: AccountAuthenticationService) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)

        val idP = IdP.from(userRequest.clientRegistration.registrationId)
        val oAuth2Credential = OAuth2Credential(idP, oauth2User.name)
        val account = accountAuthenticationService.findOrElseCreateAccount(oAuth2Credential)

        return OAuth2UserAccount(account.id, oauth2User)
    }
}

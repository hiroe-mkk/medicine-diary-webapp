package example.presentation.shared.springframework.security.oauth2

import example.application.service.account.*
import example.domain.model.account.*
import org.springframework.security.oauth2.client.userinfo.*
import org.springframework.security.oauth2.core.user.*
import org.springframework.stereotype.*

@Component
class OAuth2UserAccountService(private val accountService: AccountService) : DefaultOAuth2UserService() {
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oauth2User = super.loadUser(userRequest)
        val credential = OAuth2Credential(extractIdP(userRequest), extractSubject(oauth2User))
        val account = accountService.findOrElseCreateAccount(credential)
        return OAuth2UserAccount(account.id, oauth2User)
    }

    private fun extractSubject(oAuth2User: OAuth2User): String = oAuth2User.attributes["id"].toString()

    private fun extractIdP(userRequest: OAuth2UserRequest): IdP {
        return IdP.from(userRequest.clientRegistration.registrationId)
    }
}
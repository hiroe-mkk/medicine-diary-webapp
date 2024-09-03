package example.presentation.shared.springframework.security.oauth2.oidc

import example.application.service.account.*
import example.domain.model.account.*
import org.springframework.security.oauth2.client.oidc.userinfo.*
import org.springframework.security.oauth2.core.oidc.user.*
import org.springframework.stereotype.*

@Component
class OidcUserAccountService(private val accountService: AccountService) : OidcUserService() {
    override fun loadUser(userRequest: OidcUserRequest): OidcUser {
        val oidcUser = super.loadUser(userRequest)

        val idP = IdP.from(userRequest.clientRegistration.registrationId)
        val oAuth2Credential = OAuth2Credential(idP, oidcUser.subject)
        val account = accountService.findOrElseCreateAccount(oAuth2Credential)

        return OidcUserAccount(account.id, oidcUser)
    }
}

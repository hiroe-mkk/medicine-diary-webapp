package example.presentation.shared.springframework.security.oauth2.oidc

import example.domain.model.account.*
import example.presentation.shared.springframework.security.oauth2.*
import org.springframework.security.core.*
import org.springframework.security.oauth2.core.oidc.user.*

class OidcUserAccount(id: AccountId,
                      private val oidcUser: OidcUser) : OAuth2UserAccount(id, oidcUser),
                                                        OidcUser by oidcUser {
    override fun getAttributes(): MutableMap<String, Any> {
        return oidcUser.attributes
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return oidcUser.authorities
    }

    override fun getName(): String {
        return oidcUser.name
    }
}

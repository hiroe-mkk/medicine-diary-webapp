package example.presentation.shared.springframework.security.oauth2.oidc

import example.domain.model.account.*
import example.presentation.shared.springframework.security.oauth2.*
import org.springframework.security.oauth2.core.oidc.user.*

class OidcUserAccount(id: AccountId,
                      oidcUser: OidcUser) : OAuth2UserAccount(id, oidcUser),
                                            OidcUser by oidcUser
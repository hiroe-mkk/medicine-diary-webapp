package example.presentation.shared.springframework.security.oauth2

import example.domain.model.account.*
import example.presentation.shared.springframework.security.*
import org.springframework.security.oauth2.core.oidc.user.*
import org.springframework.security.oauth2.core.user.*

open class OAuth2UserAccount(override val id: AccountId,
                             oAuth2User: OidcUser) : AuthenticatedAccount,
                                                     OAuth2User by oAuth2User
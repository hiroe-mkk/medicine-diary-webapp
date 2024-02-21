package example.testhelper.springframework.security

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.presentation.shared.springframework.security.oauth2.*
import example.presentation.shared.springframework.security.oauth2.oidc.*
import example.testhelper.factory.*
import org.springframework.boot.test.context.*
import org.springframework.security.core.authority.*
import org.springframework.security.core.context.*
import org.springframework.security.oauth2.client.authentication.*
import org.springframework.security.oauth2.core.oidc.*
import org.springframework.security.oauth2.core.oidc.user.*
import org.springframework.security.test.context.support.*
import java.time.*

/**
 * テスト用の AuthenticatedAccount でのテスト実行をエミュレートするアノテーション
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
@WithSecurityContext(factory = WithMockAuthenticatedAccount.TestOAuth2UserAccountFactory::class)
annotation class WithMockAuthenticatedAccount(val accountId: String = "6ccd780c-baba-1026-9564-5b8c656024db") {
    @TestComponent
    class TestOAuth2UserAccountFactory(private val accountRepository: AccountRepository,
                                       private val profileRepository: ProfileRepository)
        : WithSecurityContextFactory<WithMockAuthenticatedAccount> {
        override fun createSecurityContext(annotation: WithMockAuthenticatedAccount): SecurityContext {
            val accountId = AccountId(annotation.accountId)
            val account = accountRepository.findById(accountId) ?: createAccount(accountId)
            return authenticate(account)
        }

        private fun createAccount(accountId: AccountId): Account {
            val (account, profile) = TestAccountFactory.create(accountId)
            accountRepository.save(account)
            profileRepository.save(profile)
            return account
        }

        private fun authenticate(account: Account): SecurityContext {
            val oauth2UserAccount = createOAuth2UserAccount(account)
            val securityContext = SecurityContextHolder.getContext()
            securityContext.authentication = OAuth2AuthenticationToken(oauth2UserAccount,
                                                                       oauth2UserAccount.authorities,
                                                                       (account.credential as OAuth2Credential).idP.registrationId)
            return securityContext
        }

        private fun createOAuth2UserAccount(account: Account): OAuth2UserAccount {
            val authorities = setOf(SimpleGrantedAuthority("SCOPE_openid"))
            val claims: Map<String, Any> = mapOf("sub" to (account.credential as OAuth2Credential).subject)
            val oidcUser = DefaultOidcUser(authorities,
                                           OidcIdToken("testOidcIdToken", Instant.MIN, Instant.MAX, claims))
            return OidcUserAccount(account.id, oidcUser)
        }
    }
}
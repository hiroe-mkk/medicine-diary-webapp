package example.domain.model.contact

import example.domain.shared.type.*

/**
 * お問い合わせフォーム
 */
data class ContactForm(val emailAddress: EmailAddress,
                       val name: String,
                       val message: String)
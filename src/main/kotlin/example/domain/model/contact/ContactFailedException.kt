package example.domain.model.contact

import example.domain.shared.exception.*

class ContactFailedException(val contactForm: ContactForm, cause: Throwable? = null)
    : DomainException("問い合わせの受付に失敗しました。", null, cause)

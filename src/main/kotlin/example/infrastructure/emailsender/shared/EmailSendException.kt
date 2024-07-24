package example.infrastructure.emailsender.shared

import example.infrastructure.shared.exception.*

/**
 * メールの送信に失敗したことを示す例外
 */
class EmailSendException(val email: Email, cause: Throwable? = null)
    : InfrastructureException("Failed to send email. (To: ${email.header.subject}, Subject: '${email.header.subject}')",
                              cause)

package example.domain.model.sharedgroup

import example.application.service.sharedgroup.*
import example.domain.shared.exception.*

class SharedGroupInviteEmailSendFailedException(val sharedGroupInviteForm: SharedGroupInviteForm,
                                                cause: Throwable? = null)
    : DomainException("共有グループ招待メールの送信に失敗しました。", null, cause)

package example.application.service.sharedgroup

import example.domain.model.account.profile.*
import example.domain.shared.type.*

data class SharedGroupInviteForm(val emailAddress: EmailAddress,
                                 val inviteCode: String,
                                 val expirationPeriodText: String,
                                 val inviter: Username?)

package example.application.service.sharedgroup

interface SharedGroupInviteEmailSender {
    fun send(sharedGroupInviteForm: SharedGroupInviteForm)
}

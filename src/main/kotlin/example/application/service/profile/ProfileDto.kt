package example.application.service.profile

import example.domain.model.account.profile.*

data class ProfileDto(val username: Username) {
    companion object {
        fun from(profile: Profile): ProfileDto = ProfileDto(profile.username)
    }
}
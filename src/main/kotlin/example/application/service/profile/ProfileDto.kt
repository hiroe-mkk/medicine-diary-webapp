package example.application.service.profile

import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*

data class ProfileDto(val username: Username,
                      val profileImageURL: ProfileImageURL?) {
    companion object {
        fun from(profile: Profile): ProfileDto = ProfileDto(profile.username,
                                                            profile.profileImageURL)
    }
}
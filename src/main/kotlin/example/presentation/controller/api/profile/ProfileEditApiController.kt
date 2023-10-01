package example.presentation.controller.api.profile

import example.application.service.*
import example.application.service.account.*
import example.application.service.profile.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile")
class ProfileEditApiController(private val profileService: ProfileService,
                               private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザー名を変更する
     */
    @PostMapping("/username/change")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeUsername(@Validated usernameChangeCommand: UsernameChangeCommand) {
        profileService.changeUsername(usernameChangeCommand,
                                      userSessionProvider.getUserSession())
    }

    /**
     * プロフィール画像を変更する
     */
    @PostMapping("/profileimage/change")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeProfileImage(@Validated profileImageChangeCommand: ProfileImageChangeCommand) {
        profileService.changeProfileImage(profileImageChangeCommand,
                                          userSessionProvider.getUserSession())
    }
}
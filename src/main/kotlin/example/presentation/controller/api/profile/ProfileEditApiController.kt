package example.presentation.controller.api.profile

import example.application.service.*
import example.application.service.account.*
import example.application.service.profile.*
import example.application.service.profileimage.*
import example.application.shared.command.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile")
class ProfileEditApiController(private val profileService: ProfileService,
                               private val profileImageService: ProfileImageService,
                               private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザー名を変更する
     */
    @PostMapping("/username/change")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeUsername(@Validated usernameEditCommand: UsernameEditCommand) {
        profileService.changeUsername(usernameEditCommand,
                                      userSessionProvider.getUserSession())
    }

    /**
     * プロフィール画像を変更する
     */
    @PostMapping("/profileimage/change")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeProfileImage(@Validated imageUploadCommand: ImageUploadCommand) {
        profileImageService.changeProfileImage(imageUploadCommand,
                                               userSessionProvider.getUserSession())
    }
}
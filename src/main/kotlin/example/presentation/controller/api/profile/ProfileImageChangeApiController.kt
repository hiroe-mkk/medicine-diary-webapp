package example.presentation.controller.api.profile

import example.application.service.profile.*
import example.application.shared.command.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile/image/change")
class ProfileImageChangeApiController(private val profileUpdateService: ProfileUpdateService,
                                      private val userSessionProvider: UserSessionProvider) {
    /**
     * プロフィール画像を変更する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeProfileImage(@Validated imageUploadCommand: ImageUploadCommand) {
        profileUpdateService.changeProfileImage(imageUploadCommand,
                                                userSessionProvider.getUserSessionOrElseThrow())
    }
}

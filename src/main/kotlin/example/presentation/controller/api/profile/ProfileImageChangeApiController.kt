package example.presentation.controller.api.profile

import example.application.service.profileimage.*
import example.application.shared.command.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile/profileimage/change")
class ProfileImageChangeApiController(private val profileImageService: ProfileImageService,
                                      private val userSessionProvider: UserSessionProvider) {
    /**
     * プロフィール画像を変更する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeProfileImage(@Validated imageUploadCommand: ImageUploadCommand) {
        profileImageService.changeProfileImage(imageUploadCommand,
                                               userSessionProvider.getUserSession())
    }
}
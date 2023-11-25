package example.presentation.controller.api.profile

import example.application.service.profileimage.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile/image/delete")
class ProfileImageDeletionApiController(private val profileImageService: ProfileImageService,
                                        private val userSessionProvider: UserSessionProvider) {
    /**
     * プロフィール画像を削除する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProfileImage() {
        profileImageService.deleteProfileImage(userSessionProvider.getUserSessionOrElseThrow())
    }
}
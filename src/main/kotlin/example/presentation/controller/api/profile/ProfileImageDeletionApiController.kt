package example.presentation.controller.api.profile

import example.application.service.profile.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile/image/delete")
class ProfileImageDeletionApiController(private val profileUpdateService: ProfileUpdateService,
                                        private val userSessionProvider: UserSessionProvider) {
    /**
     * プロフィール画像を削除する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProfileImage() {
        profileUpdateService.deleteProfileImage(userSessionProvider.getUserSessionOrElseThrow())
    }
}

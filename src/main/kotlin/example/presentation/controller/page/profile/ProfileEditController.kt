package example.presentation.controller.page.profile

import example.application.service.profile.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/profile/edit")
class ProfileEditController(private val profileService: ProfileService,
                            private val userSessionProvider: UserSessionProvider) {
    /**
     * プロフィール編集画面を表示する
     */
    @GetMapping
    fun displayProfileEditPage(model: Model): String {
        val userSession = userSessionProvider.getUserSession()
        val profile = profileService.findProfile(userSession)
        model.addAttribute("profile", profile)
        return "profile/edit"
    }
}
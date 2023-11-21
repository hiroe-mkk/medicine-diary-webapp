package example.presentation.controller.page

import example.application.service.profile.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/setting")
@SessionAttributes(types = [LastRequestedPagePath::class])
class SettingController(private val profileService: ProfileService,
                        private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/setting")

    /**
     * 設定画面を表示する
     */
    @GetMapping
    fun displaySettingPage(model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        val profile = profileService.findProfile(userSession)
        model.addAttribute("profile", profile)
        return "setting"
    }
}
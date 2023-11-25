package example.presentation.controller.page

import example.application.service.profile.*
import example.application.service.sharedgroup.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
@SessionAttributes(value = ["lastRequestedPagePath"])
class HomePageController(private val profileService: ProfileService,
                         private val sharedGroupService: SharedGroupService,
                         private val userSessionProvider: UserSessionProvider) {
    /**
     * ホーム画面を表示する
     */
    @GetMapping
    fun displayHomePage(model: Model): String {
        val userSession = userSessionProvider.getUserSession()
        if (userSession != null) {
            model.addAttribute("profile", profileService.findProfile(userSession))
            model.addAttribute("isParticipatingInSharedGroup",
                               sharedGroupService.isParticipatingInSharedGroup(userSession))
        }

        model.addAttribute("lastRequestedPagePath", "/")
        return "home"
    }
}
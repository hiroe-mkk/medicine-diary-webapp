package example.presentation.controller.page

import example.application.service.profile.*
import example.application.service.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/")
class HomePageController(private val profileQueryService: ProfileQueryService,
                         private val sharedGroupQueryService: SharedGroupQueryService,
                         private val userSessionProvider: UserSessionProvider,
                         private val lastRequestedPage: LastRequestedPage) {
    /**
     * ホーム画面を表示する
     */
    @GetMapping
    fun displayHomePage(model: Model): String {
        val userSession = userSessionProvider.getUserSession()
        if (userSession != null) {
            model.addAttribute("profile", profileQueryService.findProfile(userSession))
            model.addAttribute("joinedSharedGroupId",
                               sharedGroupQueryService.getJoinedSharedGroup(userSession))
        }

        lastRequestedPage.path = "/"
        return "home"
    }
}

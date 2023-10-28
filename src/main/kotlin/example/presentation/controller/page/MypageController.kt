package example.presentation.controller.page

import example.application.query.sharedgroup.*
import example.application.service.profile.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/mypage")
@SessionAttributes(types = [LastRequestedPagePath::class])
class MypageController(private val profileService: ProfileService,
                       private val sharedGroupQueryService: SharedGroupQueryService,
                       private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/mypage")

    /**
     * マイページ画面を表示する
     */
    @GetMapping
    fun displayMypagePage(model: Model): String {
        val userSession = userSessionProvider.getUserSession()
        val profile = profileService.findProfile(userSession)
        model.addAttribute("profile", profile)

        val sharedGroups = sharedGroupQueryService.findSharedGroupDetails(userSession)
        model.addAttribute("participatingSharedGroup", sharedGroups.participatingSharedGroup)
        return "mypage"
    }
}
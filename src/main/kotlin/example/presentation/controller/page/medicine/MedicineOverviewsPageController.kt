package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import jakarta.servlet.http.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines")
@SessionAttributes(value = ["lastRequestedPagePath"])
class MedicineOverviewsPageController(private val sharedGroupService: SharedGroupService,
                                      private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬一覧画面を表示する
     */
    @GetMapping
    fun displayMedicineOverviewsPage(model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("isParticipatingInSharedGroup",
                           sharedGroupService.isParticipatingInSharedGroup(userSession))

        model.addAttribute("lastRequestedPagePath", "/medicines")
        return "medicine/overviews"
    }
}
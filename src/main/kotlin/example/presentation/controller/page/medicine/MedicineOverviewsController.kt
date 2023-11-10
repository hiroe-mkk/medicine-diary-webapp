package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines")
@SessionAttributes(types = [LastRequestedPagePath::class])
class MedicineOverviewsController(private val medicineService: MedicineService,
                                  private val sharedGroupService: SharedGroupService,
                                  private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/medicines")

    /**
     * 薬概要一覧画面を表示する
     */
    @GetMapping
    fun displayMedicineOverviewsPage(model: Model): String {
        val medicineOverviews = medicineService.findMedicineOverviews(userSessionProvider.getUserSession())
        model.addAttribute("medicineOverviews", medicineOverviews)
        model.addAttribute("isParticipatingInSharedGroup",
                           sharedGroupService.isParticipatingInSharedGroup(userSessionProvider.getUserSession()))
        return "medicine/overviews"
    }
}
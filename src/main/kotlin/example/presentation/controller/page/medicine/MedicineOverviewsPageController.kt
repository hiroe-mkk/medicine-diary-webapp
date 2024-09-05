package example.presentation.controller.page.medicine

import example.application.query.medicine.*
import example.application.service.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines")
class MedicineOverviewsPageController(private val sharedGroupService: SharedGroupService,
                                      private val userSessionProvider: UserSessionProvider,
                                      private val lastRequestedPage: LastRequestedPage) {
    /**
     * 薬一覧画面を表示する
     */
    @GetMapping
    fun displayMedicineOverviewsPage(medicineFilter: MedicineFilter, model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("isJoinedSharedGroup",
                           sharedGroupService.isJoinedSharedGroup(userSession))
        model.addAttribute("medicineFilter", medicineFilter)

        lastRequestedPage.path = "/medicines"
        return "medicine/overviews"
    }
}

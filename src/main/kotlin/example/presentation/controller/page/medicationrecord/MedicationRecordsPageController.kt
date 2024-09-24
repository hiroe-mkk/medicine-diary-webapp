package example.presentation.controller.page.medicationrecord

import example.application.service.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medication-records")
class MedicationRecordsPageController(private val sharedGroupQueryService: SharedGroupQueryService,
                                      private val userSessionProvider: UserSessionProvider,
                                      private val lastRequestedPage: LastRequestedPage) {
    /**
     * 服用記録一覧画面を表示する
     */
    @GetMapping
    fun displayMedicationRecordsPage(model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("joinedSharedGroupId", sharedGroupQueryService.getJoinedSharedGroupId(userSession))

        lastRequestedPage.path = "/medication-records"
        return "medicationrecord/list"
    }
}

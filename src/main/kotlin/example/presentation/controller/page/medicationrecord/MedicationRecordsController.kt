package example.presentation.controller.page.medicationrecord

import example.application.service.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medication-records")
@SessionAttributes(value = ["lastRequestedPagePath"])
class MedicationRecordsController(private val sharedGroupService: SharedGroupService,
                                  private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録一覧画面を表示する
     */
    @GetMapping
    fun displayMedicationRecordsPage(model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("isParticipatingInSharedGroup", sharedGroupService.isParticipatingInSharedGroup(userSession))

        model.addAttribute("lastRequestedPagePath", "/medicines")
        return "medicationrecord/list"
    }
}
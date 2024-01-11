package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.domain.model.medicine.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines/{medicineId}")
@SessionAttributes(value = ["lastRequestedPagePath"])
class MedicineDetailPageController(private val medicineService: MedicineService,
                                   private val sharedGroupService: SharedGroupService,
                                   private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬詳細画面を表示する
     */
    @GetMapping
    fun displayMedicineDetailPage(@PathVariable medicineId: MedicineId,
                                  model: Model,
                                  lastRequestedPagePath: LastRequestedPagePath?): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("medicine", medicineService.findMedicine(medicineId, userSession))
        model.addAttribute("isParticipatingInSharedGroup",
                           sharedGroupService.isParticipatingInSharedGroup(userSession))
        model.addAttribute("isAvailableMedicine", medicineService.isAvailableMedicine(medicineId, userSession))

        model.addAttribute("lastRequestedPagePath", "/medicines/${medicineId}")
        return "medicine/detail"
    }
}
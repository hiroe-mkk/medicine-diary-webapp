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
@SessionAttributes(types = [LastRequestedPagePath::class])
class MedicineDetailController(private val medicineService: MedicineService,
                               private val sharedGroupService: SharedGroupService,
                               private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(@PathVariable medicineId: MedicineId): LastRequestedPagePath {
        return LastRequestedPagePath("/medicines/${medicineId}")
    }

    /**
     * 薬詳細画面を表示する
     */
    @GetMapping
    fun displayMedicineDetailPage(@PathVariable medicineId: MedicineId, model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("medicine", medicineService.findMedicine(medicineId, userSession))
        model.addAttribute("isAvailableMedicine", medicineService.isAvailableMedicine(medicineId, userSession))
        model.addAttribute("isParticipatingInSharedGroup", sharedGroupService.isParticipatingInSharedGroup(userSession))
        return "medicine/detail"
    }
}
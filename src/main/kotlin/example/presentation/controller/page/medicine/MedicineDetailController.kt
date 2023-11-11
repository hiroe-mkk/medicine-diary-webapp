package example.presentation.controller.page.medicine

import example.application.service.medicine.*
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
                               private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(@PathVariable medicineId: MedicineId): LastRequestedPagePath {
        return LastRequestedPagePath("/medicines/${medicineId}")
    }

    /**
     * 薬詳細画面を表示する
     */
    @GetMapping
    fun displayMedicineDetailPage(@PathVariable medicineId: MedicineId,
                                  model: Model,
                                  lastRequestedPagePath: LastRequestedPagePath?): String {
        val userSession = userSessionProvider.getUserSession()
        model.addAttribute("medicine", medicineService.findMedicine(medicineId, userSession))
        model.addAttribute("isAvailableMedicine", medicineService.isAvailableMedicine(medicineId, userSession))
        val lastRequestPagePath = lastRequestPagePath(medicineId, lastRequestedPagePath)
        model.addAttribute("lastRequestPagePath", lastRequestPagePath)
        return "medicine/detail"
    }

    private fun lastRequestPagePath(medicineId: MedicineId, lastRequestedPagePath: LastRequestedPagePath?): String {
        return if (lastRequestedPagePath?.value == null || lastRequestedPagePath.value == "/medicines/${medicineId}") {
            "/medicines"
        } else {
            lastRequestedPagePath.value
        }
    }
}
package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.application.service.sharedgroup.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines/{medicineId}")
class MedicineDetailPageController(private val medicineQueryService: MedicineQueryService,
                                   private val sharedGroupQueryService: SharedGroupQueryService,
                                   private val userSessionProvider: UserSessionProvider,
                                   private val lastRequestedPage: LastRequestedPage) {
    /**
     * 薬詳細画面を表示する
     */
    @GetMapping
    fun displayMedicineDetailPage(@PathVariable medicineId: MedicineId,
                                  model: Model): String {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("medicine", medicineQueryService.findMedicine(medicineId, userSession))
        model.addAttribute("isJoinedSharedGroup",
                           sharedGroupQueryService.isJoinedSharedGroup(userSession))
        model.addAttribute("isAvailableMedicine", medicineQueryService.isAvailableMedicine(medicineId, userSession))

        lastRequestedPage.path = "/medicines/${medicineId}"
        return "medicine/detail"
    }
}

package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines/{medicineId}")
class MedicineDetailController(private val medicineService: MedicineService,
                               private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬詳細画面を表示する
     */
    @GetMapping
    fun displayMedicineDetailPage(@PathVariable medicineId: MedicineId, model: Model): String {
        val medicineDetail = medicineService.findMedicineDetail(medicineId,
                                                                userSessionProvider.getUserSession())
        model.addAttribute("medicineDetail", medicineDetail)
        return "medicine/detail"
    }
}
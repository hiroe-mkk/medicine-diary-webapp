package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/medicines")
class MedicineOverviewsController(private val medicineService: MedicineService,
                                  private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬一覧画面を表示する
     */
    @GetMapping
    fun displayMedicineOverviewsPage(model: Model): String {
        val medicineOverviews = medicineService.findAllMedicineOverviews(userSessionProvider.getUserSession())
        model.addAttribute("medicineOverviews", medicineOverviews)
        return "medicine/overviews"
    }
}
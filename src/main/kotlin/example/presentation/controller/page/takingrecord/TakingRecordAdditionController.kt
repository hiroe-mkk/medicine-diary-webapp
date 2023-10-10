package example.presentation.controller.page.takingrecord

import example.application.service.medicine.*
import example.application.service.takingrecord.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/takingrecords/add")
class TakingRecordAdditionController(private val medicineService: MedicineService,
                                     private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("medicines")
    fun medicines(): Map<MedicineId, String> {
        val medicines = medicineService.findAllMedicineOverviews(userSessionProvider.getUserSession())
        return medicines.associate { Pair(it.medicineId, it.name) }
    }

    @ModelAttribute("title")
    fun title(): String = "服用記録追加"

    @ModelAttribute("executePath")
    fun executePath(): String = "/takingrecords/add"

    /**
     * 服用記録追加画面を表示する
     */
    @GetMapping
    fun displayTakingRecordAdditionPage(model: Model): String {
        model.addAttribute("form", TakingRecordEditCommand.initialize())
        return "takingrecord/form"
    }
}
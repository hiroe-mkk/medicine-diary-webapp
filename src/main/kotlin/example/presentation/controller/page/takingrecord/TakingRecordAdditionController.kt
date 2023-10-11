package example.presentation.controller.page.takingrecord

import example.application.service.medicine.*
import example.application.service.takingrecord.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/takingrecords/add")
class TakingRecordAdditionController(private val medicineService: MedicineService,
                                     private val takingRecordService: TakingRecordService,
                                     private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("medicines")
    fun medicines(): Map<String, String> {
        val medicines = medicineService.findAllMedicineOverviews(userSessionProvider.getUserSession())
        return medicines.associate { Pair(it.medicineId.value, it.name) }
    }

    @ModelAttribute("conditionLevels")
    fun conditionLevels(): Array<ConditionLevel> = ConditionLevel.values()

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

    /**
     * 服用記録を追加する
     */
    @PostMapping
    fun addTakingRecord(@ModelAttribute("form") @Validated takingRecordEditCommand: TakingRecordEditCommand,
                        bindingResult: BindingResult,
                        redirectAttributes: RedirectAttributes): String {
        if (bindingResult.hasErrors()) return "takingrecord/form"

        takingRecordService.addTakingRecord(takingRecordEditCommand,
                                            userSessionProvider.getUserSession())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("服用記録の追加が完了しました。"))
        return "redirect:/mypage" // TODO: 服用記録追加画面を表示していた画面に遷移するように変更する
    }
}
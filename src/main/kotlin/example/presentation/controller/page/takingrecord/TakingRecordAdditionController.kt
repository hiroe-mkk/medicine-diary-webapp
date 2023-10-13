package example.presentation.controller.page.takingrecord

import example.application.service.medicine.*
import example.application.service.takingrecord.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.message.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import jakarta.servlet.http.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.support.*
import org.springframework.web.servlet.mvc.support.*
import java.net.*

@Controller
@RequestMapping("/takingrecords/add")
@SessionAttributes(value = ["lastRequestedPagePath"])
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
                        redirectAttributes: RedirectAttributes,
                        lastRequestedPagePath: LastRequestedPagePath?): String {
        if (bindingResult.hasErrors()) return "takingrecord/form"

        try {
            takingRecordService.addTakingRecord(takingRecordEditCommand,
                                                userSessionProvider.getUserSession())
            redirectAttributes.addFlashAttribute("resultMessage",
                                                 ResultMessage.info("服用記録の追加が完了しました。"))
        } catch (ex: MedicineNotFoundException) {
            redirectAttributes.addFlashAttribute("resultMessage",
                                                 ResultMessage.error("服用記録の追加に失敗しました。"))
        }

        val redirectPath = lastRequestedPagePath?.value ?: "/mypage"
        return "redirect:${redirectPath}"
    }
}
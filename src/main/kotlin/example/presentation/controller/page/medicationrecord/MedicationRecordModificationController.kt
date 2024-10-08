package example.presentation.controller.page.medicationrecord

import example.application.service.medicationrecord.*
import example.application.service.medicine.*
import example.domain.model.medicationrecord.*
import example.domain.shared.exception.*
import example.domain.shared.message.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/medication-records/{medicationRecordId}/modify")
class MedicationRecordModificationController(private val medicationRecordQueryService: MedicationRecordQueryService,
                                             private val medicationRecordModificationService: MedicationRecordModificationService,
                                             private val userSessionProvider: UserSessionProvider,
                                             private val lastRequestedPage: LastRequestedPage) {
    @ModelAttribute("conditionLevels")
    fun conditionLevels(): Array<ConditionLevel> = ConditionLevel.entries.toTypedArray()

    @ModelAttribute("title")
    fun title(): String = "服用記録修正"

    @ModelAttribute("executePath")
    fun executePath(@PathVariable medicationRecordId: MedicationRecordId): String =
            "/medication-records/${medicationRecordId}/modify"

    /**
     * 服用記録修正画面を表示する
     */
    @GetMapping
    fun displayMedicationRecordModificationPage(@PathVariable medicationRecordId: MedicationRecordId,
                                                model: Model): String {
        if (!medicationRecordQueryService.isValidMedicationRecordId(medicationRecordId))
            throw InvalidEntityIdException(medicationRecordId)

        val command = medicationRecordModificationService.getModificationEditCommand(medicationRecordId,
                                                                                     userSessionProvider.getUserSessionOrElseThrow())
        model.addAttribute("form", command)
        return "medicationrecord/form"
    }

    /**
     * 服用記録を修正する
     */
    @PostMapping
    fun modifyMedicationRecord(@PathVariable medicationRecordId: MedicationRecordId,
                               @ModelAttribute("form") @Validated medicationRecordEditCommand: MedicationRecordEditCommand,
                               bindingResult: BindingResult,
                               redirectAttributes: RedirectAttributes): String {
        if (!medicationRecordQueryService.isValidMedicationRecordId(medicationRecordId))
            throw InvalidEntityIdException(medicationRecordId)
        if (bindingResult.hasErrors()) return "medicationrecord/form"

        try {
            medicationRecordModificationService.modifyMedicationRecord(medicationRecordId,
                                                                       medicationRecordEditCommand,
                                                                       userSessionProvider.getUserSessionOrElseThrow())
        } catch (ex: MedicineNotFoundException) {
            // トランザクションの関係で、薬の存在チェックは @Validated ではなく、この段階で例外処理を行う
            bindingResult.rejectValue("takenMedicine",
                                      "NotFoundTakenMedicine",
                                      arrayOf<Any>("takenMedicine"), "※お薬が見つかりませんでした。")

            return "medicationrecord/form"
        }

        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("服用記録の修正が完了しました。"))
        return "redirect:${lastRequestedPage.path}"
    }
}

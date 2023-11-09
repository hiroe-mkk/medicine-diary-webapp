package example.presentation.controller.page.takingrecord

import example.application.service.medicine.*
import example.application.service.takingrecord.*
import example.domain.model.takingrecord.*
import example.domain.shared.message.*
import example.presentation.shared.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/takingrecords/{takingRecordId}/modify")
@SessionAttributes(value = ["lastRequestedPagePath"])
class TakingRecordModificationController(private val takingRecordService: TakingRecordService,
                                         private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("conditionLevels")
    fun conditionLevels(): Array<ConditionLevel> = ConditionLevel.values()

    @ModelAttribute("title")
    fun title(): String = "服用記録修正"

    @ModelAttribute("executePath")
    fun executePath(@PathVariable takingRecordId: TakingRecordId): String = "/takingrecords/${takingRecordId}/modify"

    /**
     * 服用記録修正画面を表示する
     */
    @GetMapping
    fun displayTakingRecordModificationPage(@PathVariable takingRecordId: TakingRecordId,
                                            model: Model): String {
        val command = takingRecordService.getInitializedTakingRecordEditCommand(takingRecordId,
                                                                                userSessionProvider.getUserSession())
        model.addAttribute("form", command)
        return "takingrecord/form"
    }

    /**
     * 服用記録を修正する
     */
    @PostMapping
    fun modifyTakingRecord(@PathVariable takingRecordId: TakingRecordId,
                           @ModelAttribute("form") @Validated takingRecordEditCommand: TakingRecordEditCommand,
                           bindingResult: BindingResult,
                           redirectAttributes: RedirectAttributes,
                           lastRequestedPagePath: LastRequestedPagePath?): String {
        if (bindingResult.hasErrors()) return "takingrecord/form"

        try {
            takingRecordService.modifyTakingRecord(takingRecordId,
                                                   takingRecordEditCommand,
                                                   userSessionProvider.getUserSession())
            redirectAttributes.addFlashAttribute("resultMessage",
                                                 ResultMessage.info("服用記録の修正が完了しました。"))
        } catch (ex: MedicineNotFoundException) {
            redirectAttributes.addFlashAttribute("resultMessage",
                                                 ResultMessage.error("服用記録の修正に失敗しました。"))
        }

        val redirectPath = lastRequestedPagePath?.value ?: "/mypage"
        return "redirect:${redirectPath}"
    }
}
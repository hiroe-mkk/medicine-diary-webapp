package example.presentation.controller.page.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/medicines/{medicineId}/delete")
class MedicineDeletionController(private val medicineDeletionService: MedicineDeletionService,
                                 private val medicineQueryService: MedicineQueryService,
                                 private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬を削除する
     */
    @PostMapping
    fun deleteMedicine(@PathVariable medicineId: MedicineId,
                       redirectAttributes: RedirectAttributes): String {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        medicineDeletionService.deleteMedicine(medicineId,
                                               userSessionProvider.getUserSessionOrElseThrow())
        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("お薬の削除が完了しました。"))
        return "redirect:/medicines"
    }
}

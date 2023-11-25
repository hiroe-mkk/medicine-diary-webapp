package example.presentation.controller.api.medicine

import example.application.service.medicineimage.*
import example.application.shared.command.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}/image/delete")
class MedicineImageDeletionApiController(private val medicineImageService: MedicineImageService,
                                         private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬画像を削除する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private fun deleteMedicineImage(@PathVariable medicineId: MedicineId) {
        medicineImageService.deleteMedicineImage(medicineId,
                                                 userSessionProvider.getUserSessionOrElseThrow())
    }
}
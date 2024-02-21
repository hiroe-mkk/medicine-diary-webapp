package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.application.service.medicineimage.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}/image/delete")
class MedicineImageDeletionApiController(private val medicineService: MedicineService,
                                         private val medicineImageService: MedicineImageService,
                                         private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬画像を削除する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMedicineImage(@PathVariable medicineId: MedicineId) {
        if (!medicineService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        medicineImageService.deleteMedicineImage(medicineId,
                                                 userSessionProvider.getUserSessionOrElseThrow())
    }
}
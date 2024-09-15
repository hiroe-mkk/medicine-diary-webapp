package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.application.shared.command.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}/image/change")
class MedicineImageChangeApiController(private val medicineQueryService: MedicineQueryService,
                                       private val medicineImageService: MedicineImageService,
                                       private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬画像を変更する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeMedicineImage(@PathVariable medicineId: MedicineId,
                            @Validated imageUploadCommand: ImageUploadCommand) {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        medicineImageService.changeMedicineImage(medicineId,
                                                 imageUploadCommand,
                                                 userSessionProvider.getUserSessionOrElseThrow())
    }
}

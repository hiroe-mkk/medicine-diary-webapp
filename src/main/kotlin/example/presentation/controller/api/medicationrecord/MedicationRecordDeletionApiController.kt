package example.presentation.controller.api.medicationrecord

import example.application.service.medicationrecord.*
import example.domain.model.medicationrecord.*
import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medication-records/{medicationRecordId}/delete")
class MedicationRecordDeletionApiController(private val medicationRecordService: MedicationRecordService,
                                            private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録を削除する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteMedicationRecord(@PathVariable medicationRecordId: MedicationRecordId) {
        if (!medicationRecordService.isValidMedicationRecordId(medicationRecordId))
            throw InvalidEntityIdException(medicationRecordId)

        medicationRecordService.deleteMedicationRecord(medicationRecordId,
                                                       userSessionProvider.getUserSessionOrElseThrow())
    }
}
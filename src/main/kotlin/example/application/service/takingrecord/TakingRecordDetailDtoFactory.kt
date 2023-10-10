package example.application.service.takingrecord

import example.application.service.medicine.*
import example.application.service.profile.*
import example.application.service.takingrecord.TakingRecordDetailDto.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import org.springframework.stereotype.*

@Component
class TakingRecordDetailDtoFactory(private val profileRepository: ProfileRepository,
                                   private val medicineRepository: MedicineRepository) {
    fun create(takingRecord: TakingRecord): TakingRecordDetailDto {
        val profile = profileRepository.findByAccountId(takingRecord.recorder)
                      ?: throw ProfileNotFoundException(takingRecord.recorder)
        val medicine = medicineRepository.findById(takingRecord.medicineId)
                       ?: throw MedicineNotFoundException(takingRecord.medicineId)

        return TakingRecordDetailDto(takingRecord.id,
                                     Recorder(takingRecord.recorder,
                                              profile.username,
                                              profile.profileImageURL),
                                     TakenMedicine(takingRecord.medicineId,
                                                   medicine.name),
                                     takingRecord.dose,
                                     takingRecord.symptoms,
                                     takingRecord.note,
                                     takingRecord.takenAt)
    }
}
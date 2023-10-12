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
        val medicine = medicineRepository.findById(takingRecord.takenMedicine)
                       ?: throw MedicineNotFoundException(takingRecord.takenMedicine)
        return TakingRecordDetailDto(takingRecord.id,
                                     TakenMedicine(takingRecord.takenMedicine,
                                                   medicine.name,
                                                   takingRecord.dose,
                                                   medicine.dosageAndAdministration.takingUnit),
                                     takingRecord.followUp,
                                     takingRecord.note,
                                     takingRecord.takenAt,
                                     Recorder(takingRecord.recorder,
                                              profile.username,
                                              profile.profileImageURL))
    }
}
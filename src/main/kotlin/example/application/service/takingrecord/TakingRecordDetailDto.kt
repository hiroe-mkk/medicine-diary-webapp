package example.application.service.takingrecord

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import java.time.*

data class TakingRecordDetailDto(val takingRecordId: TakingRecordId,
                                 val takenMedicine: TakenMedicine,
                                 val followUp: FollowUp,
                                 val note: Note,
                                 val takenAt: LocalDateTime,
                                 val recorder: Recorder) {

    data class TakenMedicine(val medicineId: MedicineId,
                             val name: String,
                             val dose: Dose,
                             val takingUnit: String)

    data class Recorder(val accountId: AccountId,
                        val username: Username,
                        val profileImageURL: ProfileImageURL?)
}
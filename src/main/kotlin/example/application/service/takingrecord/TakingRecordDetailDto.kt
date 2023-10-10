package example.application.service.takingrecord

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import java.time.*

data class TakingRecordDetailDto(val id: TakingRecordId,
                                 val recorder: Recorder,
                                 val medicine: TakenMedicine,
                                 val dose: Dose,
                                 val symptoms: Symptoms,
                                 val note: Note,
                                 val takenAt: LocalDateTime) {
    data class Recorder(val accountId: AccountId,
                        val username: Username,
                        val profileImageURL: ProfileImageURL?)

    data class TakenMedicine(val medicineId: MedicineId,
                             val name: String)
}
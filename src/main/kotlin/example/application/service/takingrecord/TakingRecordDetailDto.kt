package example.application.service.takingrecord

import com.fasterxml.jackson.annotation.*
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

    data class Recorder(val accountId: AccountId,
                        val username: Username,
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        val profileImageURL: ProfileImageURL?)

    data class TakenMedicine(val medicineId: MedicineId,
                             val name: String,
                             val dose: Dose,
                             val takingUnit: String)
}
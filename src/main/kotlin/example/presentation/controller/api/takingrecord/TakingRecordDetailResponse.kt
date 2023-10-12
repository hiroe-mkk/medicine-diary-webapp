package example.presentation.controller.api.takingrecord

import com.fasterxml.jackson.annotation.*
import example.application.service.takingrecord.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import java.time.*
import java.time.format.*

class TakingRecordDetailResponse(val takingRecordId: String,
                                 val takenMedicine: TakenMedicine,
                                 val symptom: String,
                                 val beforeTaking: String,
                                 @JsonInclude(JsonInclude.Include.NON_NULL)
                                 val afterTaking: String?,
                                 val note: String,
                                 val takenAt: String,
                                 val recorder: Recorder) {
    companion object {
        fun from(takingRecordDetailDto: TakingRecordDetailDto): TakingRecordDetailResponse {
            return TakingRecordDetailResponse(
                    takingRecordDetailDto.takingRecordId.value,
                    TakenMedicine.from(takingRecordDetailDto.takenMedicine),
                    takingRecordDetailDto.followUp.symptom,
                    takingRecordDetailDto.followUp.beforeTaking.str,
                    takingRecordDetailDto.followUp.afterTaking?.str,
                    takingRecordDetailDto.note.toString(),
                    DateTimeFormatter
                        .ofPattern("yyyy/M/d HH:mm")
                        .format(takingRecordDetailDto.takenAt),
                    Recorder.from(takingRecordDetailDto.recorder))
        }
    }

    class Recorder(val accountId: String,
                   val username: String,
                   @JsonInclude(JsonInclude.Include.NON_NULL)
                   val profileImageURL: String?) {
        companion object {
            fun from(recorder: TakingRecordDetailDto.Recorder): Recorder {
                return Recorder(recorder.accountId.value,
                                recorder.username.value,
                                recorder.profileImageURL?.toURL())
            }
        }
    }

    class TakenMedicine(val medicineId: String,
                        val name: String,
                        val dose: String) {
        companion object {
            fun from(takenMedicine: TakingRecordDetailDto.TakenMedicine): TakenMedicine {
                return TakenMedicine(takenMedicine.medicineId.value,
                                     takenMedicine.name,
                                     "${takenMedicine.dose}${takenMedicine.takingUnit}")
            }
        }
    }
}
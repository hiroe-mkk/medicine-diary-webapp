package example.presentation.controller.api.takingrecord

import com.fasterxml.jackson.annotation.*
import example.application.service.takingrecord.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.domain.shared.type.*
import java.time.*
import java.time.format.*

class JSONTakingRecordDetailResponse(val takingRecordId: String,
                                     val takenMedicine: JSONTakenMedicine,
                                     val symptom: String,
                                     val beforeTaking: String,
                                     @JsonInclude(JsonInclude.Include.NON_NULL)
                                     val afterTaking: String?,
                                     val note: String,
                                     val takenAt: String,
                                     val recorder: JSONRecorder) {
    companion object {
        fun from(takingRecordDetailDto: TakingRecordDetailDto): JSONTakingRecordDetailResponse {
            return JSONTakingRecordDetailResponse(
                    takingRecordDetailDto.takingRecordId.value,
                    JSONTakenMedicine.from(takingRecordDetailDto.takenMedicine),
                    takingRecordDetailDto.followUp.symptom,
                    takingRecordDetailDto.followUp.beforeTaking.str,
                    takingRecordDetailDto.followUp.afterTaking?.str,
                    takingRecordDetailDto.note.toString(),
                    DateTimeFormatter
                        .ofPattern("yyyy/M/d HH:mm")
                        .format(takingRecordDetailDto.takenAt),
                    JSONRecorder.from(takingRecordDetailDto.recorder))
        }
    }

    class JSONRecorder(val accountId: String,
                       val username: String,
                       @JsonInclude(JsonInclude.Include.NON_NULL)
                       val profileImageURL: String?) {
        companion object {
            fun from(recorder: TakingRecordDetailDto.Recorder): JSONRecorder {
                return JSONRecorder(recorder.accountId.value,
                                    recorder.username.value,
                                    recorder.profileImageURL?.toURL())
            }
        }
    }

    class JSONTakenMedicine(val medicineId: String,
                            val name: String,
                            val dose: String) {
        companion object {
            fun from(takenMedicine: TakingRecordDetailDto.TakenMedicine): JSONTakenMedicine {
                return JSONTakenMedicine(takenMedicine.medicineId.value,
                                         takenMedicine.name,
                                         "${takenMedicine.dose}${takenMedicine.takingUnit}")
            }
        }
    }
}
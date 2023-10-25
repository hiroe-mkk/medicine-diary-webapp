package example.presentation.controller.api.takingrecord

import com.fasterxml.jackson.annotation.*
import example.application.query.shared.type.*
import example.application.query.takingrecord.*
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
        fun from(takingRecordDetail: TakingRecordDetail): JSONTakingRecordDetailResponse {
            return JSONTakingRecordDetailResponse(
                    takingRecordDetail.takingRecordId.value,
                    JSONTakenMedicine.from(takingRecordDetail.medicineId,
                                           takingRecordDetail.name,
                                           takingRecordDetail.dose,
                                           takingRecordDetail.takingUnit),
                    takingRecordDetail.followUp.symptom,
                    takingRecordDetail.followUp.beforeTaking.str,
                    takingRecordDetail.followUp.afterTaking?.str,
                    takingRecordDetail.note.toString(),
                    DateTimeFormatter
                        .ofPattern("yyyy/M/d HH:mm")
                        .format(takingRecordDetail.takenAt),
                    JSONRecorder.from(takingRecordDetail.recorder))
        }
    }

    class JSONRecorder(val accountId: String,
                       val username: String,
                       @JsonInclude(JsonInclude.Include.NON_NULL)
                       val profileImageURL: String?) {
        companion object {
            fun from(recorder: User): JSONRecorder {
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
            fun from(medicineId: MedicineId,
                     name: String,
                     dose: Dose,
                     takingUnit: String): JSONTakenMedicine {
                return JSONTakenMedicine(medicineId.value,
                                         name,
                                         "${dose}${takingUnit}")
            }
        }
    }
}
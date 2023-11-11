package example.presentation.controller.api.takingrecord

import com.fasterxml.jackson.annotation.*
import example.application.query.takingrecord.*
import example.application.service.takingrecord.*
import example.application.shared.usersession.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.presentation.controller.api.takingrecord.JSONTakingRecordsResponse.*
import org.springframework.data.domain.*
import java.time.*
import java.time.format.*

class JSONTakingRecordsResponse(val number: Int,
                                val totalPages: Int,
                                val takingRecords: List<JSONTakingRecord>) {
    companion object {
        fun from(page: Page<DisplayTakingRecord>, userSession: UserSession): JSONTakingRecordsResponse {
            return JSONTakingRecordsResponse(page.number,
                                             page.totalPages,
                                             page.content.map {
                                                 JSONTakingRecord.from(it, userSession)
                                             })
        }
    }

    class JSONTakingRecord(val takingRecordId: String,
                           val takenMedicine: JSONTakenMedicine,
                           val followUp: JSONFollowUp,
                           val note: String,
                           val takenAt: String,
                           val recorder: JSONRecorder,
                           val isOwned: Boolean) {
        companion object {
            fun from(displayTakingRecord: DisplayTakingRecord, userSession: UserSession): JSONTakingRecord {
                return JSONTakingRecord(displayTakingRecord.takingRecordId.value,
                                        JSONTakenMedicine.from(displayTakingRecord),
                                        JSONFollowUp.from(displayTakingRecord),
                                        displayTakingRecord.note.value,
                                        DateTimeFormatter
                                            .ofPattern("yyyy/M/d HH:mm")
                                            .format(displayTakingRecord.takenAt),
                                        JSONRecorder.from(displayTakingRecord),
                                        displayTakingRecord.recorder.accountId == userSession.accountId)
            }
        }

        class JSONFollowUp(val symptom: String,
                           val beforeTaking: String,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val afterTaking: String?) {
            companion object {
                fun from(displayTakingRecord: DisplayTakingRecord): JSONFollowUp {
                    return JSONFollowUp(displayTakingRecord.followUp.symptom,
                                        displayTakingRecord.followUp.beforeTaking.str,
                                        displayTakingRecord.followUp.afterTaking?.str)
                }
            }
        }

        class JSONTakenMedicine(val medicineId: String,
                                val medicineName: String) {
            companion object {
                fun from(displayTakingRecord: DisplayTakingRecord): JSONTakenMedicine {
                    return JSONTakenMedicine(displayTakingRecord.medicineId.value,
                                             displayTakingRecord.medicineName.value)
                }
            }
        }

        class JSONRecorder(val accountId: String,
                           val username: String,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val profileImageURL: String?) {
            companion object {
                fun from(displayTakingRecord: DisplayTakingRecord): JSONRecorder {
                    return JSONRecorder(displayTakingRecord.recorder.accountId.value,
                                        displayTakingRecord.recorder.username.value,
                                        displayTakingRecord.recorder.profileImageURL?.toURL())
                }
            }
        }
    }
}
package example.presentation.controller.api.takingrecord

import com.fasterxml.jackson.annotation.*
import example.application.query.takingrecord.*
import example.application.service.takingrecord.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.presentation.controller.api.takingrecord.JSONTakingRecordOverviewsResponse.*
import org.springframework.data.domain.*
import java.time.*
import java.time.format.*

class JSONTakingRecordOverviewsResponse(val number: Int,
                                        val totalPages: Int,
                                        val takingRecordOverviews: List<JSONTakingRecordOverview>) {
    companion object {
        fun from(takingRecordOverviews: Page<TakingRecordOverview>): JSONTakingRecordOverviewsResponse {
            return JSONTakingRecordOverviewsResponse(takingRecordOverviews.number,
                                                     takingRecordOverviews.totalPages,
                                                     takingRecordOverviews.content.map {
                                                         JSONTakingRecordOverview.from(it)
                                                     })
        }
    }

    class JSONTakingRecordOverview(val takingRecordId: String,
                                   val beforeTaking: String,
                                   @JsonInclude(JsonInclude.Include.NON_NULL)
                                   val afterTaking: String?,
                                   val takenAt: String,
                                   val takenMedicine: JSONTakenMedicine,
                                   val recorder: JSONRecorder) {
        companion object {
            fun from(takingRecordOverview: TakingRecordOverview): JSONTakingRecordOverview {
                return JSONTakingRecordOverview(takingRecordOverview.takingRecordId.value,
                                                takingRecordOverview.beforeTaking.str,
                                                takingRecordOverview.afterTaking?.str,
                                                DateTimeFormatter
                                                    .ofPattern("yyyy/M/d HH:mm")
                                                    .format(takingRecordOverview.takenAt),
                                                JSONTakenMedicine(takingRecordOverview.medicineId.value,
                                                                  takingRecordOverview.medicineName.value),
                                                JSONRecorder(takingRecordOverview.recorder.accountId.value,
                                                             takingRecordOverview.recorder.username.value,
                                                             takingRecordOverview.recorder.profileImageURL?.toURL()))
            }
        }

        class JSONTakenMedicine(val medicineId: String,
                                val medicineName: String)

        class JSONRecorder(val accountId: String,
                           val username: String,
                           @JsonInclude(JsonInclude.Include.NON_NULL)
                           val profileImageURL: String?)
    }
}
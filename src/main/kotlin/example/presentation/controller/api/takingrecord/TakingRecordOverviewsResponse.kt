package example.presentation.controller.api.takingrecord

import com.fasterxml.jackson.annotation.*
import example.application.query.takingrecord.*
import example.application.service.takingrecord.*
import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import example.presentation.controller.api.takingrecord.TakingRecordOverviewsResponse.*
import org.springframework.data.domain.*
import java.time.*
import java.time.format.*

class TakingRecordOverviewsResponse(val number: Int,
                                    val totalPages: Int,
                                    val takingRecordOverviews: List<JSONTakingRecordOverview>) {
    companion object {
        fun from(takingRecordOverviews: Page<TakingRecordOverview>): TakingRecordOverviewsResponse {
            return TakingRecordOverviewsResponse(takingRecordOverviews.number,
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
                                   val takenMedicine: TakenMedicine,
                                   val recorder: Recorder) {
        data class Recorder(val accountId: String,
                            val username: String,
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            val profileImageURL: String?)

        data class TakenMedicine(val medicineId: String,
                                 val name: String)

        companion object {
            fun from(takingRecordOverview: TakingRecordOverview): JSONTakingRecordOverview {
                return JSONTakingRecordOverview(takingRecordOverview.takingRecordId.value,
                                                takingRecordOverview.beforeTaking.str,
                                                takingRecordOverview.afterTaking?.str,
                                                DateTimeFormatter
                                                    .ofPattern("yyyy/M/d HH:mm")
                                                    .format(takingRecordOverview.takenAt),
                                                TakenMedicine(takingRecordOverview.medicineId.value,
                                                              takingRecordOverview.medicineName),
                                                Recorder(takingRecordOverview.accountId.value,
                                                         takingRecordOverview.username.value,
                                                         takingRecordOverview.profileImageURL?.toURL()))
            }
        }
    }
}
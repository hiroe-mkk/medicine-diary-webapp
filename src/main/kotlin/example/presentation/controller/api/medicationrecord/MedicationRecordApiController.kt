package example.presentation.controller.api.medicationrecord

import example.application.query.medicationrecord.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.data.web.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import java.time.*

@Controller
@RequestMapping("/api/medication-records")
class MedicationRecordApiController(private val jsonMedicationRecordQueryService: JSONMedicationRecordQueryService,
                                    private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録一覧を取得する
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getMedicationRecords(medicationRecordFilter: MedicationRecordFilter,
                             @PageableDefault(page = 0,
                                              size = 10) pageable: Pageable): JSONMedicationRecords {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        return jsonMedicationRecordQueryService.findJSONMedicationRecordsPage(userSession,
                                                                              medicationRecordFilter,
                                                                              pageable)
    }
}
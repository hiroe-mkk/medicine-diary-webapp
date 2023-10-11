package example.presentation.controller.api.takingrecord

import example.application.query.takingrecord.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.data.web.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/takingrecords")
class TakingRecordOverviewsApiController(private val takingRecordOverviewQueryService: TakingRecordOverviewQueryService,
                                         private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録概要一覧を取得する
     */
    @GetMapping(params = ["medicineid"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getTakingRecordOverviews(@RequestParam(name = "medicineid", required = true) medicineId: MedicineId,
                                 @PageableDefault(page = 0,
                                                  size = 10) pageable: Pageable): TakingRecordOverviewsResponse {
        val takingRecordOverviews = takingRecordOverviewQueryService.findTakingRecordDetailsByMedicineId(medicineId,
                                                                                                         userSessionProvider.getUserSession(),
                                                                                                         pageable)
        return TakingRecordOverviewsResponse.from(takingRecordOverviews)
    }
}
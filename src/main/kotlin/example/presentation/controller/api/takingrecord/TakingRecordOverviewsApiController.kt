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
class TakingRecordOverviewsApiController(private val takingRecordQueryService: TakingRecordQueryService,
                                         private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録概要一覧を取得する
     */
    @GetMapping(params = ["medicineid"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getTakingRecordOverviews(@RequestParam(name = "medicineid", required = true) medicineId: MedicineId,
                                 @PageableDefault(page = 0,
                                                  size = 10) pageable: Pageable): JSONTakingRecordOverviewsResponse {
        val userSession = userSessionProvider.getUserSession()
        val filter = TakingRecordOverviewsFilter(medicineId, emptySet(), null, null)
        val takingRecordOverviews =
                takingRecordQueryService.findTakingRecordOverviewsPage(userSession, filter, pageable)
        return JSONTakingRecordOverviewsResponse.from(takingRecordOverviews)
    }
}
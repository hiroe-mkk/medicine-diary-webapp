package example.presentation.controller.api.takingrecord

import example.application.query.takingrecord.*
import example.application.service.takingrecord.*
import example.domain.model.takingrecord.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/takingrecords/{takingRecordId}")
class TakingRecordDetailApiController(private val takingRecordQueryService: TakingRecordQueryService,
                                      private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録詳細を取得する
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getTakingRecordDetail(@PathVariable takingRecordId: TakingRecordId): JSONTakingRecordDetailResponse {
        val takingRecordDetail = takingRecordQueryService.findTakingRecordDetail(takingRecordId,
                                                                                 userSessionProvider.getUserSession())
        return JSONTakingRecordDetailResponse.from(takingRecordDetail)
    }
}
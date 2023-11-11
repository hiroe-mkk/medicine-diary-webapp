package example.presentation.controller.api.takingrecord

import example.application.query.takingrecord.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.data.web.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import java.time.*

@Controller
@RequestMapping("/api/takingrecords")
class TakingRecordApiController(private val takingRecordQueryService: TakingRecordQueryService,
                                private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録一覧を取得する
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getTakingRecords(takingRecordFilter: TakingRecordFilter,
                         @PageableDefault(page = 0,
                                          size = 10) pageable: Pageable): JSONTakingRecordsResponse {
        val userSession = userSessionProvider.getUserSession()
        val page = takingRecordQueryService.findTakingRecordsPage(userSession,
                                                                  takingRecordFilter,
                                                                  pageable)
        return JSONTakingRecordsResponse.from(page)
    }
}
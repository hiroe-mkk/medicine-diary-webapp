package example.presentation.controller.api.takingrecord

import example.application.service.takingrecord.*
import example.domain.model.takingrecord.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/takingrecords/{takingRecordId}/delete")
class TakingRecordDeletionApiController(private val takingRecordService: TakingRecordService,
                                        private val userSessionProvider: UserSessionProvider) {
    /**
     * 服用記録を削除する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTakingRecord(@PathVariable takingRecordId: TakingRecordId) {
        takingRecordService.deleteTakingRecord(takingRecordId,
                                               userSessionProvider.getUserSession())
    }
}
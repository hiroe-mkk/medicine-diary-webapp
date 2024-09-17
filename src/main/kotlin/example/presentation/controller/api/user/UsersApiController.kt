package example.presentation.controller.api.user

import example.application.query.user.*
import example.application.service.sharedgroup.*
import example.domain.model.sharedgroup.*
import example.domain.shared.exception.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/users")
class UsersApiController(private val sharedGroupQueryService: SharedGroupQueryService,
                         private val jsonUserQueryService: JSONUserQueryService) {
    /**
     * 共有グループのメンバー覧を取得する
     */
    @GetMapping(params = ["sharedGroupId"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getSharedGroupMember(@RequestParam sharedGroupId: SharedGroupId): JSONUsers {
        if (!sharedGroupQueryService.isValidSharedGroupId(sharedGroupId)) throw InvalidEntityIdException(sharedGroupId)

        return jsonUserQueryService.getSharedGroupMembers(sharedGroupId)
    }
}

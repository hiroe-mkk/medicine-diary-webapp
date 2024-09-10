package example.presentation.controller.api.user

import example.application.query.user.*
import example.domain.model.sharedgroup.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/users")
class UsersApiController(private val jsonUserQueryService: JSONUserQueryService) {
    /**
     * 共有グループのメンバー覧を取得する
     */
    @GetMapping(params = ["sharedGroupId"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getSharedGroupMember(@RequestParam sharedGroupId: SharedGroupId): JSONUsers {
        // TODO: sharedGroupId の有効性チェック
        return jsonUserQueryService.findJSONSharedGroupMember(sharedGroupId)
    }
}

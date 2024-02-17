package example.presentation.controller.api.sharedgroup

import example.application.query.sharedgroup.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/shared-group")
class SharedGroupApiController(private val jsonSharedGroupQueryService: JSONSharedGroupQueryService,
                               private val userSessionProvider: UserSessionProvider) {
    /**
     * 共有グループを取得する
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun sharedGroup(): JSONSharedGroups {
        return jsonSharedGroupQueryService.findJSONSharedGroup(userSessionProvider.getUserSessionOrElseThrow())
    }
}
package example.presentation.controller.api.user

import example.application.query.user.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/users")
class UsersApiController(private val userQueryService: UserQueryService,
                         private val userSessionProvider: UserSessionProvider) {
    /**
     * キーワードでユーザー一覧を取得する
     */
    @GetMapping(params = ["keyword"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUsersByKeyword(@RequestParam(name = "keyword", required = false) keyword: String?): JSONUsersResponse {
        val users = userQueryService.findByKeyword(keyword ?: "", userSessionProvider.getUserSession())
        return JSONUsersResponse.from(users)
    }

    /**
     * メンバーユーザー一覧を取得する
     */
    @GetMapping(params = ["member"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getMemberUsers(): JSONUsersResponse {
        val users = userQueryService.findMemberUsers(userSessionProvider.getUserSession())
        return JSONUsersResponse.from(users)
    }
}
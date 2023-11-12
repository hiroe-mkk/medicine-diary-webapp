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
    fun getUsersByKeyword(@RequestParam(name = "keyword", required = false) keyword: String?): JSONUsers {
        return userQueryService.findJSONUsersByKeyword(keyword ?: "", userSessionProvider.getUserSession())
    }

    /**
     * メンバーユーザー一覧を取得する
     */
    @GetMapping(params = ["member"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getMemberUsers(): JSONUsers {
        return userQueryService.findMemberJSONUsers(userSessionProvider.getUserSession())
    }
}
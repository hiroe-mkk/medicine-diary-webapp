package example.presentation.controller.api.user

import example.application.query.user.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/users")
class UsersApiController(private val JSONUserQueryService: JSONUserQueryService,
                         private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザーを取得する
     */
    @GetMapping(params = ["own"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUser(): JSONUser {
        return JSONUserQueryService.findUser(userSessionProvider.getUserSession())
    }

    /**
     * キーワードでユーザー一覧を取得する
     */
    @GetMapping(params = ["keyword"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUsersByKeyword(@RequestParam(name = "keyword", required = false) keyword: String?): JSONUsers {
        return JSONUserQueryService.findJSONUsersByKeyword(keyword ?: "", userSessionProvider.getUserSession())
    }

    /**
     * メンバーユーザー一覧を取得する
     */
    @GetMapping(params = ["members"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getMemberUsers(): JSONUsers {
        return JSONUserQueryService.findMemberJSONUsers(userSessionProvider.getUserSession())
    }
}
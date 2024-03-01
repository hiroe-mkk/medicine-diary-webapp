package example.presentation.controller.api.user

import example.application.query.user.*
import example.presentation.shared.usersession.*
import jakarta.validation.constraints.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/users")
class UsersApiController(private val jsonUserQueryService: JSONUserQueryService,
                         private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザーを取得する
     */
    @GetMapping(params = ["self"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUser(): JSONUser {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        return jsonUserQueryService.findJSONUser(userSession.accountId)
    }

    /**
     * キーワードでユーザー一覧を取得する
     */
    @GetMapping(params = ["keyword"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUsersByKeyword(@Validated keywordFilter: KeywordFilter): JSONUsers {
        return jsonUserQueryService.findJSONUsersByKeyword(keywordFilter.keyword,
                                                           userSessionProvider.getUserSessionOrElseThrow())
    }

    /**
     * メンバーユーザー一覧を取得する
     */
    @GetMapping(params = ["members"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getMemberUsers(): JSONUsers {
        return jsonUserQueryService.findJSONMemberUsers(userSessionProvider.getUserSessionOrElseThrow())
    }
}
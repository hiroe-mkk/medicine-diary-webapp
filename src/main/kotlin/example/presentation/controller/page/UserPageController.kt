package example.presentation.controller.page

import example.application.query.user.*
import example.application.service.account.*
import example.domain.model.account.*
import example.domain.shared.exception.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/users/{accountId}")
class UserPageController(private val userQueryService: UserQueryService,
                         private val accountService: AccountService,
                         private val userSessionProvider: UserSessionProvider,
                         private val lastRequestedPage: LastRequestedPage) {
    /**
     * ユーザー画面を表示する
     */
    @GetMapping
    fun displayUserPage(@PathVariable accountId: AccountId, model: Model): String {
        if (!accountService.isValidAccountId(accountId)) throw InvalidEntityIdException(accountId)

        model.addAttribute("user", userQueryService.findMemberUser(accountId,
                                                                   userSessionProvider.getUserSessionOrElseThrow()))

        lastRequestedPage.path = "/users/${accountId}"
        return "user"
    }
}

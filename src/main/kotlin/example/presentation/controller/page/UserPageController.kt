package example.presentation.controller.page

import example.application.query.user.*
import example.application.service.sharedgroup.*
import example.domain.model.account.*
import example.domain.model.medicine.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/users/{accountId}")
@SessionAttributes(value = ["lastRequestedPagePath"])
class UserPageController(private val userQueryService: UserQueryService,
                         private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザー画面を表示する
     */
    @GetMapping
    fun displayUserPage(@PathVariable accountId: AccountId, model: Model): String {
        model.addAttribute("user", userQueryService.findMemberUser(accountId,
                                                                   userSessionProvider.getUserSessionOrElseThrow()))

        model.addAttribute("lastRequestedPagePath", "/users/${accountId}");
        return "user"
    }
}
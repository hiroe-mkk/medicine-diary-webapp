package example.presentation.controller.page.account

import example.application.service.account.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import jakarta.servlet.http.*
import org.springframework.security.core.context.*
import org.springframework.security.web.authentication.logout.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/account/delete")
class AccountDeletionController(private val accountDeletionService: AccountDeletionService,
                                private val userSessionProvider: UserSessionProvider) {
    /**
     * アカウントを削除する
     */
    @PostMapping
    fun deleteAccount(httpServletRequest: HttpServletRequest,
                      httpServletResponse: HttpServletResponse,
                      redirectAttributes: RedirectAttributes): String {
        accountDeletionService.deleteAccount(userSessionProvider.getUserSessionOrElseThrow())

        val logoutHandler = SecurityContextLogoutHandler()
        logoutHandler.logout(httpServletRequest,
                             httpServletResponse,
                             SecurityContextHolder.getContext().authentication)

        redirectAttributes.addFlashAttribute("resultMessage",
                                             ResultMessage.info("アカウントの削除が完了しました。",
                                                                "ご利用ありがとうございました。"))

        return "redirect:/"
    }
}

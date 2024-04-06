package example.presentation.controller.page

import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/calendar")
class CalendarPageController(private val userSessionProvider: UserSessionProvider,
                             private val lastRequestedPage: LastRequestedPage) {
    /**
     * カレンダー画面を表示する
     */
    @GetMapping
    fun displayCalendarPage(model: Model): String {
        val userSession = userSessionProvider.getUserSessionOrElseThrow()
        model.addAttribute("accountId", userSession.accountId)

        lastRequestedPage.path = "/calendar"
        return "calendar"
    }
}

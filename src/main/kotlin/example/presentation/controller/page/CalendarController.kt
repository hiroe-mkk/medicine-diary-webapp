package example.presentation.controller.page

import example.application.service.profile.*
import example.application.service.sharedgroup.*
import example.presentation.shared.session.*
import example.presentation.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.ui.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/calendar")
@SessionAttributes(types = [LastRequestedPagePath::class])
class CalendarController(private val userSessionProvider: UserSessionProvider) {
    @ModelAttribute("lastRequestedPagePath")
    fun lastRequestedPagePath(): LastRequestedPagePath = LastRequestedPagePath("/calendar")

    /**
     * カレンダー画面を表示する
     */
    @GetMapping
    fun displayCalendarPage(model: Model): String {
        val userSession = userSessionProvider.getUserSession()
        model.addAttribute("accountId", userSession.accountId)
        return "calendar"
    }
}
package example.presentation.controller.api.profile

import example.application.service.*
import example.application.service.account.*
import example.application.service.profile.*
import example.application.service.profileimage.*
import example.application.shared.command.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/profile/username/change")
class UsernameChangeApiController(private val profileService: ProfileService,
                                  private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザー名を変更する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeUsername(@Validated usernameEditCommand: UsernameEditCommand) {
        profileService.changeUsername(usernameEditCommand,
                                      userSessionProvider.getUserSessionOrElseThrow())
    }
}
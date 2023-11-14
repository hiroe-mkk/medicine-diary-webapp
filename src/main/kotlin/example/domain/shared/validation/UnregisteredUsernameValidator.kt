package example.domain.shared.validation

import example.domain.model.account.profile.*
import jakarta.validation.*
import org.springframework.stereotype.*

@Component
class UnregisteredUsernameValidator(private val usernameChangeValidationService: UsernameChangeValidationService) :
        ConstraintValidator<UnregisteredUsername, String> {
    override fun isValid(username: String?, context: ConstraintValidatorContext): Boolean {
        return usernameChangeValidationService.canUsernameChange(Username(username ?: ""))
    }
}
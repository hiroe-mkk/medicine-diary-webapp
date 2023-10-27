package example.domain.shared.validation

import example.domain.model.account.profile.*
import jakarta.validation.*
import org.springframework.stereotype.*

@Component
class UnregisteredUsernameValidator(private val profileDomainService: ProfileDomainService) :
        ConstraintValidator<UnregisteredUsername, String> {
    override fun isValid(username: String?, context: ConstraintValidatorContext): Boolean {
        return profileDomainService.isUsernameChangeableState(Username(username ?: ""))
    }
}
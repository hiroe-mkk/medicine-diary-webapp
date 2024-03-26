package example.domain.shared.validation

import example.domain.model.account.profile.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.validation.*
import org.springframework.boot.test.context.*
import org.springframework.context.annotation.*
import org.springframework.validation.*

@MyBatisRepositoryTest
@Import(ValidationAutoConfiguration::class)
internal class UnusedUsernameTest(@Autowired private val validator: Validator,
                                  @Autowired private val testAccountInserter: TestAccountInserter) {
    @TestConfiguration
    class Configuration {
        @Bean
        fun profileDomainService(profileRepository: ProfileRepository): UsernameChangeValidationService {
            return UsernameChangeValidationService(profileRepository)
        }
    }

    @Test
    fun unregisteredUsernameIdInvalid() {
        //given:
        val duplicateUsername = "testUsername"
        testAccountInserter.insertAccountAndProfile(username = Username(duplicateUsername))
        val form = Form(duplicateUsername)
        val bindException = BindException(form, "form")

        //when:
        validator.validate(form, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("username")).isTrue
    }

    @Test
    fun registeredUsernameIsInvalid() {
        //given:
        val username = "testUsername"
        val form = Form(username)
        val bindException = BindException(form, "form")

        //when:
        validator.validate(form, bindException)

        //then:
        val bindingResult = bindException.bindingResult
        assertThat(bindingResult.hasFieldErrors("username")).isFalse
    }

    data class Form(@field:UnregisteredUsername
                    private val username: String)
}
package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.domain.shared.message.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.*

@Controller
@RequestMapping("/api/medicines/register")
class MedicineRegistrationApiController(private val medicineService: MedicineService,
                                        private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬を登録する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    fun registerMedicine(@Validated medicineBasicInfoInputCommand: MedicineBasicInfoInputCommand): MedicineRegistrationResultResponse {
        val medicineId = medicineService.registerMedicine(medicineBasicInfoInputCommand,
                                                          userSessionProvider.getUserSession())
        return MedicineRegistrationResultResponse.create(medicineId)
    }
}
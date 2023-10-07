package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}")
class MedicineImageChangeApiController(private val medicineService: MedicineService,
                                       private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬画像を変更する
     */
    @PostMapping("/medicineimage/change")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeMedicineImage(@PathVariable medicineId: MedicineId,
                            @Validated medicineImageChangeCommand: MedicineImageChangeCommand) {
        medicineService.changeMedicineImage(medicineId,
                                            medicineImageChangeCommand,
                                            userSessionProvider.getUserSession())
    }
}
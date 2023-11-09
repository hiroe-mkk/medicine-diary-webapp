package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.controller.api.takingrecord.*
import example.presentation.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.data.web.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines")
class MedicineOverviewsApiController(private val medicineService: MedicineService,
                                     private val userSessionProvider: UserSessionProvider) {
    /**
     * ユーザーの薬概要一覧を取得する
     */
    @GetMapping(params = ["user"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getUserMedicineOverviews(): JSONMedicineOverviewsResponse {
        val medicines = medicineService.findUserMedicineOverviews(userSessionProvider.getUserSession())
        return JSONMedicineOverviewsResponse.from(medicines)
    }
}
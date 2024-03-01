package example.presentation.controller.api.medicine

import example.application.query.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines")
class MedicineOverviewsApiController(private val jsonMedicineOverviewsQueryService: JSONMedicineOverviewsQueryService,
                                     private val userSessionProvider: UserSessionProvider) {
    /**
     * 薬概要一覧を取得する
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getMedicineOverviews(@Validated medicineFilter: MedicineFilter): JSONOwnerBaseMedicineOverviews {
        return jsonMedicineOverviewsQueryService.findMedicineOverviews(userSession(), medicineFilter)
    }

    /**
     * 服用可能な薬概要一覧を取得する
     */
    @GetMapping(params = ["available"])
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    fun getAvailableMedicineOverviews(): JSONMedicineOverviews {
        return jsonMedicineOverviewsQueryService.findJSONAvailableMedicineOverviews(userSession())
    }

    private fun userSession() = userSessionProvider.getUserSessionOrElseThrow()
}
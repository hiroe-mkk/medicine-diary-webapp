package example.presentation.controller.api.medicine

import example.application.query.medicine.*
import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.presentation.controller.api.medicationrecord.*
import example.presentation.shared.usersession.*
import org.springframework.data.domain.*
import org.springframework.data.web.*
import org.springframework.http.*
import org.springframework.stereotype.*
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
    fun getMedicineOverviews(): JSONOwnerBaseMedicineOverviews {
        return jsonMedicineOverviewsQueryService.findMedicineOverviews(userSession())
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
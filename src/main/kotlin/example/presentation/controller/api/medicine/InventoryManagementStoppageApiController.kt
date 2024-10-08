package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}/inventory/stop")
class InventoryManagementStoppageApiController(private val medicineQueryService: MedicineQueryService,
                                               private val medicineUpdateService: MedicineUpdateService,
                                               private val userSessionProvider: UserSessionProvider) {
    /**
     * 在庫管理を終了する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun stopInventoryManagement(@PathVariable medicineId: MedicineId) {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        medicineUpdateService.stopInventoryManagement(medicineId,
                                                      userSessionProvider.getUserSessionOrElseThrow())
    }
}

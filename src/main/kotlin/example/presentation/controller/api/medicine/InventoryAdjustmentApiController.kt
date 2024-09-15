package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.domain.model.medicine.*
import example.domain.shared.exception.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}/inventory/adjust")
class InventoryAdjustmentApiController(private val medicineQueryService: MedicineQueryService,
                                       private val medicineUpdateService: MedicineUpdateService,
                                       private val userSessionProvider: UserSessionProvider) {
    /**
     * 在庫を修正する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun adjustInventory(@PathVariable medicineId: MedicineId,
                        @Validated inventoryAdjustmentCommand: InventoryAdjustmentCommand) {
        if (!medicineQueryService.isValidMedicineId(medicineId)) throw InvalidEntityIdException(medicineId)

        medicineUpdateService.adjustInventory(medicineId,
                                              inventoryAdjustmentCommand,
                                              userSessionProvider.getUserSessionOrElseThrow())
    }
}

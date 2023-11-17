package example.presentation.controller.api.medicine

import example.application.service.medicine.*
import example.application.shared.command.*
import example.domain.model.medicine.*
import example.presentation.shared.usersession.*
import org.springframework.http.*
import org.springframework.stereotype.*
import org.springframework.validation.annotation.*
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/api/medicines/{medicineId}/inventory/adjust")
class InventoryAdjustmentApiController(private val medicineService: MedicineService,
                                       private val userSessionProvider: UserSessionProvider) {
    /**
     * 在庫を修正する
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun adjustInventory(@PathVariable medicineId: MedicineId,
                        @Validated inventoryAdjustmentCommand: InventoryAdjustmentCommand) {
        medicineService.adjustInventory(medicineId,
                                        inventoryAdjustmentCommand,
                                        userSessionProvider.getUserSession())
    }
}
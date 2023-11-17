package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.shared.validation.*
import jakarta.validation.constraints.*
import org.springframework.format.annotation.*
import java.time.*

/**
 * 在庫の修正に利用される Command クラス
 */
data class InventoryAdjustmentCommand(@field:NotNull(message = "※使用中パッケージの残量を入力してください。")
                                      @field:MedicineQuantity
                                      val remainingQuantity: Double?,
                                      @field:NotNull(message = "※1パッケージあたりの内容量を入力してください。")
                                      @field:MedicineQuantity
                                      val quantityPerPackage: Double?,
                                      @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      val startedOn: LocalDate?,
                                      @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                      val expirationOn: LocalDate?,
                                      @field:Min(value = 0, message = "※{value}以上の数値を入力してください。")
                                      @field:Max(value = 100, message = "※{value}以下の数値を入力してください。")
                                      val unusedPackage: Int?) {
    val validatedInventory: Inventory = Inventory(remainingQuantity ?: 0.0,
                                                  quantityPerPackage ?: 0.0,
                                                  startedOn,
                                                  expirationOn,
                                                  unusedPackage ?: 0)

    companion object {
        fun initialize(inventory: Inventory?): InventoryAdjustmentCommand {
            return InventoryAdjustmentCommand(inventory?.remainingQuantity ?: 0.0,
                                              inventory?.quantityPerPackage ?: 0.0,
                                              inventory?.startedOn ?: null,
                                              inventory?.expirationOn ?: null,
                                              inventory?.unusedPackage ?: 0)
        }
    }
}
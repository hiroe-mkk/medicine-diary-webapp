package example.application.query.medicationrecord

import example.domain.model.account.*
import example.domain.model.medicine.*
import org.hibernate.validator.constraints.*
import org.springframework.format.annotation.*
import java.time.*

data class MedicationRecordFilter(@field:UUID val medicine: String?,
                                  @field:UUID val account: String?,
                                  @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  val start: LocalDate?,
                                  @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  val end: LocalDate?) {
    val validatedMedicine: MedicineId? = medicine?.let { MedicineId(it) }
    val validatedAccount: AccountId? = account?.let { AccountId(it) }
}
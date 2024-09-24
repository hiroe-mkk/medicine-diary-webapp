package example.application.query.medicationrecord

import org.hibernate.validator.constraints.*
import org.springframework.format.annotation.*
import java.time.*

data class MedicationRecordFilter(@field:UUID val medicine: String?,
                                  @field:UUID val account: String?,
                                  @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  val start: LocalDate?,
                                  @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                  val end: LocalDate?) {
    val nonBlankMedicineId: String? = if (medicine?.isNotBlank() == true) medicine else null
    val nonBlankAccountId: String? = if (account?.isNotBlank() == true) account else null

    fun isEmpty(): Boolean {
        return nonBlankMedicineId == null && nonBlankAccountId == null && start == null && end == null
    }
}

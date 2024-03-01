package example.presentation.controller.page.medicationrecord

import example.domain.model.medicine.*
import org.hibernate.validator.constraints.*
import org.springframework.format.annotation.*
import java.time.*

data class MedicationRecordAdditionFormInitialValue(@field:UUID val medicine: String?,
                                                    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                    val date: LocalDate?) {
    val validatedMedicine: MedicineId? = medicine?.let { MedicineId(it) }
}
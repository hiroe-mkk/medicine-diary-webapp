package example.application.query.medicationrecord

import org.springframework.data.domain.*

class JSONMedicationRecords(val number: Int,
                            val totalPages: Int,
                            val medicationRecords: List<JSONMedicationRecord>) {
    companion object {
        fun from(page: Page<JSONMedicationRecord>): JSONMedicationRecords {
            return JSONMedicationRecords(page.number, page.totalPages, page.content)
        }
    }
}
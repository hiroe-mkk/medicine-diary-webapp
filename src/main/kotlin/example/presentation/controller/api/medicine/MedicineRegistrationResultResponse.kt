package example.presentation.controller.api.medicine

import example.domain.model.medicine.*

class MedicineRegistrationResultResponse(val medicineId: String) {
    companion object {
        fun create(medicineId: MedicineId): MedicineRegistrationResultResponse {
            return MedicineRegistrationResultResponse(medicineId.value)
        }
    }
}
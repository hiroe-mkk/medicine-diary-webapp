package example.application.query.medicine

import jakarta.validation.constraints.*

data class MedicineFilter(@field:Size(max = 30) val effect: String?)
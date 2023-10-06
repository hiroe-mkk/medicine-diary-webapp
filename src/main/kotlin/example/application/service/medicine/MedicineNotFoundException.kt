package example.application.service.medicine

import example.domain.model.medicine.*
import example.domain.shared.exception.*

/**
 * 薬が見つからなかったことを示す例外
 */
class MedicineNotFoundException(val medicineId: MedicineId)
    : ResourceNotFoundException("薬が見つかりませんでした。", medicineId)
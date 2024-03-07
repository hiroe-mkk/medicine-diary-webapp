package example.application.service.medicine

import example.application.shared.exception.*
import example.domain.model.medicine.*

/**
 * 薬が見つからなかったことを示す例外
 */
class MedicineNotFoundException(val medicineId: MedicineId)
    : ResourceNotFoundException("お薬が見つかりませんでした。", medicineId)
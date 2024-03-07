package example.application.service.medicationrecord

import example.application.shared.exception.*
import example.domain.model.medicationrecord.*

/**
 * 服用記録が見つからなかったことを示す例外
 */
class MedicationRecordNotFoundException(val medicationRecordId: MedicationRecordId)
    : ResourceNotFoundException("服用記録が見つかりませんでした。", medicationRecordId)
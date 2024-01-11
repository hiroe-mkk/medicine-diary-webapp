package example.application.service.medicationrecord

import example.domain.model.medicationrecord.*
import example.domain.shared.exception.*

/**
 * 服用記録が見つからなかったことを示す例外
 */
class MedicationRecordNotFoundException(val medicationRecordId: MedicationRecordId)
    : ResourceNotFoundException("服用記録が見つかりませんでした。", medicationRecordId)
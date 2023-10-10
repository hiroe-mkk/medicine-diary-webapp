package example.application.service.takingrecord

import example.domain.model.takingrecord.*
import example.domain.shared.exception.*

/**
 * 服用記録が見つからなかったことを示す例外
 */
class TakingRecordNotFoundException(val takingRecordId: TakingRecordId)
    : ResourceNotFoundException("服用記録が見つかりませんでした。", takingRecordId)
package example.domain.shared.exception

import example.domain.shared.message.*
import example.domain.shared.type.*

/**
 * 無効なエンティティ ID であることを示す例外
 */
class InvalidEntityIdException(entityId: EntityId)
    : ResultMessageNotificationException(ResultMessage.error("無効な形式のIDです。", "ID: $entityId"))
package example.domain.shared.exception

import example.domain.shared.message.*
import example.domain.shared.type.*

/**
 * リソースが見つからなかったことを示す例外
 */
abstract class ResourceNotFoundException(message: String, entityId: EntityId)
    : ResultMessageNotificationException(ResultMessage.error(message, "ID: $entityId"))
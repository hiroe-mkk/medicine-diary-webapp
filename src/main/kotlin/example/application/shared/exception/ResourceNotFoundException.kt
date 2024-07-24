package example.application.shared.exception

import example.domain.shared.type.*

/**
 * リソースが見つからなかったことを示す例外
 */
abstract class ResourceNotFoundException(message: String, entityId: EntityId)
    : ApplicationServiceException(message, "ID: $entityId")

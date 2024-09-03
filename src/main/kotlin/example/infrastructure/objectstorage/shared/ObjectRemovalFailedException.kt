package example.infrastructure.objectstorage.shared

import example.infrastructure.shared.exception.*

class ObjectRemovalFailedException(val bucketName: String, val objectName: String, cause: Throwable? = null)
    : InfrastructureException("Failed to remove object. (Bucket: ${bucketName}, Object: ${objectName})", cause)

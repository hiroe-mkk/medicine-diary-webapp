package example.infrastructure.objectstorage.shared

import example.infrastructure.shared.exception.*

class ObjectPutFailedException(val bucketName: String, val key: String, cause: Throwable? = null)
    : InfrastructureException("Failed to put object. (Bucket: ${bucketName}, Key: ${key})", cause)

package example.infrastructure.objectstorage.shared

import example.infrastructure.shared.exception.*

class ObjectCopyFailedException(val bucketName: String,
                                val sourceObjectName: String,
                                val targetObjectName: String,
                                cause: Throwable? = null)
    : InfrastructureException("Failed to copy object. (Bucket: ${bucketName}, SourceObject: $sourceObjectName, TargetObject: $targetObjectName)",
                              cause)

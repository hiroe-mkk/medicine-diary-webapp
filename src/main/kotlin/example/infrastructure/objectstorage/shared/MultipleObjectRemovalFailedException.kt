package example.infrastructure.objectstorage.shared

import example.infrastructure.shared.exception.*

class MultipleObjectRemovalFailedException(val bucketName: String,
                                           val objectNames: List<String>,
                                           val causes: List<Throwable>)
    : InfrastructureException("Failed to remove some objects. (Bucket: ${bucketName}, Object: ${objectNames})",
                              createCompositeException(causes)) {
    companion object {
        private fun createCompositeException(causes: List<Throwable>): Exception {
            val compositeException = Exception("Multiple errors occurred during image removal")
            causes.forEach { compositeException.addSuppressed(it) }
            return compositeException
        }
    }
}

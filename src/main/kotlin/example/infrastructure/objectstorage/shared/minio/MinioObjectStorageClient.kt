package example.infrastructure.objectstorage.shared.minio

import example.domain.shared.type.*
import example.infrastructure.objectstorage.shared.*
import io.minio.*
import io.minio.messages.*
import org.slf4j.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("dev-local", "dev-container")
@Component
class MinioObjectStorageClient(private val minioProperties: MinioProperties,
                               private val minioClient: MinioClient) : ObjectStorageClient {
    private val logger = LoggerFactory.getLogger(MinioObjectStorageClient::class.java)

    override fun getEndpoint(): String {
        return minioProperties.endpoint + "/" + minioProperties.bucketName
    }

    override fun put(url: URL, fileContent: FileContent) {
        val bucketName = minioProperties.bucketName
        val objectName = convertToObjectName(url)

        val startTime = System.currentTimeMillis()
        try {
            val putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .stream(fileContent.content,
                        fileContent.size.toLong(),
                        -1)
                .contentType(fileContent.type.toString())
                .build()
            minioClient.putObject(putObjectArgs)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Object put to MinIO. (Bucket: $bucketName, Key: {$objectName}) $duration ms"
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to put object to MinIO. (Bucket: $bucketName, Key: $objectName) Error: ${exception.message}"
            )
            throw ObjectPutFailedException(bucketName, objectName, exception)
        }
    }

    override fun copy(sourceURL: URL, targetURL: URL) {
        val bucketName = minioProperties.bucketName
        val sourceObjectName = convertToObjectName(sourceURL)
        val targetObjectName = convertToObjectName(targetURL)

        val startTime = System.currentTimeMillis()
        try {
            val copySource = CopySource.builder()
                .bucket(bucketName)
                .`object`(sourceObjectName)
                .build()
            val copyObjectArgs = CopyObjectArgs.builder()
                .source(copySource)
                .bucket(bucketName)
                .`object`(targetObjectName)
                .build()
            minioClient.copyObject(copyObjectArgs)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Object copied within MinIO. (Bucket: $bucketName, SourceKey: $sourceObjectName, TargetKey: $targetObjectName) $duration ms"
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to copy object within MinIO. (Bucket: $bucketName, SourceKey: $sourceObjectName, TargetKey: $targetObjectName) Error: ${exception.message}"
            )
            throw ObjectCopyFailedException(bucketName, sourceObjectName, targetObjectName, exception)
        }
    }

    override fun remove(url: URL) {
        val bucketName = minioProperties.bucketName
        val objectName = convertToObjectName(url)

        val startTime = System.currentTimeMillis()
        try {
            val removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .`object`(objectName)
                .build()
            minioClient.removeObject(removeObjectArgs)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Object remove from MinIO. (Bucket: $bucketName, Key: {$objectName}) $duration ms"
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to remove object from MinIO. (Bucket: $bucketName, Key: $objectName) Error: ${exception.message}"
            )
            throw ObjectRemovalFailedException(bucketName, objectName, exception)
        }
    }

    override fun removeAll(urls: Collection<URL>) {
        val bucketName: String = minioProperties.bucketName
        val deleteObjects = urls
            .map { DeleteObject(convertToObjectName(it)) }
            .toList()

        val startTime = System.currentTimeMillis()
        val results = minioClient.removeObjects(RemoveObjectsArgs.builder()
                                                    .bucket(bucketName)
                                                    .objects(deleteObjects)
                                                    .build())
        val failedObjectNames: MutableList<String> = mutableListOf()
        val exceptions: MutableList<Exception> = mutableListOf()
        for (result in results) {
            try {
                val error = result.get()
                failedObjectNames.add(error.objectName())
            } catch (exception: Exception) {
                exceptions.add(exception)
            }
        }
        val duration = System.currentTimeMillis() - startTime

        if (failedObjectNames.isEmpty()) {
            logger.debug("Objects removed from MinIO. (Bucket: {}, Keys: {}) {}ms",
                         bucketName, deleteObjects, duration)
        } else {
            logger.warn("Failed to remove some objects from MinIO. (Bucket: {}, Keys: {}) Errors: {}",
                        bucketName, failedObjectNames, exceptions.map(Exception::message))
            throw MultipleObjectRemovalFailedException(bucketName, failedObjectNames, exceptions)
        }
    }

    private fun convertToObjectName(url: URL): String = url.path.substring(1)
}

package example.infrastructure.objectstorage.shared.s3

import com.amazonaws.services.s3.*
import com.amazonaws.services.s3.model.*
import example.domain.shared.type.*
import example.infrastructure.objectstorage.shared.*
import org.slf4j.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("prod")
@Component
class S3ObjectStorageClient(private val s3Properties: S3Properties,
                            private val amazonS3: AmazonS3) : ObjectStorageClient {
    private val logger = LoggerFactory.getLogger(S3ObjectStorageClient::class.java)

    override fun getEndpoint(): String {
        return s3Properties.endpoint
    }

    override fun put(url: URL, fileContent: FileContent) {
        val bucketName = s3Properties.bucketName
        val key = convertToKey(url)

        val startTime = System.currentTimeMillis()
        try {
            val objectMetadata = ObjectMetadata().also {
                it.contentType = fileContent.type.toString()
                it.contentLength = fileContent.size.toLong()
            }
            val putObjectRequest = PutObjectRequest(bucketName,
                                                    key,
                                                    fileContent.content,
                                                    objectMetadata)
            amazonS3.putObject(putObjectRequest)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Object put to AWS S3. (Bucket: $bucketName, Key: {$key}) $duration ms"
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to put object to AWS S3. (Bucket: $bucketName, Key: $key) Error: ${exception.message}"
            )
            throw ObjectPutFailedException(bucketName, key, exception)
        }
    }

    override fun copy(sourceURL: URL, targetURL: URL) {
        val bucketName = s3Properties.bucketName
        val sourceKey = convertToKey(sourceURL)
        val targetKey = convertToKey(targetURL)

        val startTime = System.currentTimeMillis()
        try {
            val copyRequest = CopyObjectRequest(bucketName, sourceKey,
                                                bucketName, targetKey)
            amazonS3.copyObject(copyRequest)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Object copied within AWS S3. (Bucket: $bucketName, SourceKey: $sourceKey, TargetKey: $targetKey) $duration ms"
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to copy object within AWS S3. (Bucket: $bucketName, SourceKey: $sourceKey, TargetKey: $targetKey) Error: ${exception.message}"
            )
            throw ObjectCopyFailedException(bucketName, sourceKey, targetKey, exception)
        }
    }

    override fun remove(url: URL) {
        val bucketName = s3Properties.bucketName
        val key = convertToKey(url)

        val startTime = System.currentTimeMillis()
        try {
            amazonS3.deleteObject(bucketName, key)
            val duration = System.currentTimeMillis() - startTime

            logger.debug(
                    "Object remove to AWS S3. (Bucket: $bucketName, Key: {$key}) $duration ms"
            )
        } catch (exception: Exception) {
            logger.error(
                    "Failed to remove object to AWS S3. (Bucket: $bucketName, Key: $key) Error: ${exception.message}"
            )
            throw ObjectRemovalFailedException(bucketName, key, exception)
        }
    }

    override fun removeAll(urls: Collection<URL>) {
        val bucketName = s3Properties.bucketName
        val keys = urls.map { DeleteObjectsRequest.KeyVersion(convertToKey(it)) }.toMutableList()

        val startTime = System.currentTimeMillis()
        val deleteObjectsRequest = DeleteObjectsRequest(bucketName).withKeys(keys)
        val results = amazonS3.deleteObjects(deleteObjectsRequest)

        val failedKeys: MutableList<String> = mutableListOf()
        val exceptions: MutableList<Exception> = mutableListOf()
        for (result in results.deletedObjects) {
            try {
                failedKeys.add(result.key)
            } catch (exception: Exception) {
                exceptions.add(exception)
            }
        }
        val duration = System.currentTimeMillis() - startTime

        if (failedKeys.isEmpty()) {
            logger.debug("Objects removed from AWS S3. (Bucket: {}, Keys: {}) {}ms",
                         bucketName, results, duration)
        } else {
            logger.warn("Failed to remove some objects from AWS S3. (Bucket: {}, Keys: {}) Errors: {}",
                        bucketName, failedKeys, exceptions.map(Exception::message))
            throw MultipleObjectRemovalFailedException(bucketName, failedKeys, exceptions)
        }
    }

    private fun convertToKey(url: URL): String = url.path.substring(1)
}

package example.infrastructure.storage.shared.objectstrage.s3

import com.amazonaws.services.s3.*
import com.amazonaws.services.s3.model.*
import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*


@Profile("prod")
@Component
class S3ObjectStorageClient(private val s3Properties: S3Properties,
                            private val amazonS3: AmazonS3) : ObjectStorageClient {
    override fun getEndpoint(): String {
        return s3Properties.endpoint
    }

    override fun put(url: URL, fileContent: FileContent) {
        val objectMetadata = ObjectMetadata().also {
            it.contentType = fileContent.type.toString()
            it.contentLength = fileContent.size.toLong()
        }
        val putObjectRequest = PutObjectRequest(s3Properties.bucketName,
                                                convertToObjectName(url),
                                                fileContent.content,
                                                objectMetadata)

        amazonS3.putObject(putObjectRequest)
    }

    override fun copy(sourceURL: URL, targetURL: URL) {
        val copyRequest = CopyObjectRequest(s3Properties.bucketName, convertToObjectName(sourceURL),
                                            s3Properties.bucketName, convertToObjectName(targetURL))
        amazonS3.copyObject(copyRequest)
    }

    override fun remove(url: URL) {
        amazonS3.deleteObject(s3Properties.bucketName, convertToObjectName(url))
    }

    override fun removeAll(urls: Collection<URL>) {
        val keys = urls.map { DeleteObjectsRequest.KeyVersion(convertToObjectName(it)) }.toMutableList()
        val deleteObjectsRequest = DeleteObjectsRequest(s3Properties.bucketName)
            .withKeys(keys)
        amazonS3.deleteObjects(deleteObjectsRequest)
    }

    private fun convertToObjectName(URL: URL): String = URL.path.substring(1)
}
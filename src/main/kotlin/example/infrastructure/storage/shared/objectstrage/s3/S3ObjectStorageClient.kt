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

    override fun put(fullPath: FullPath, fileContent: FileContent) {
        val objectMetadata = ObjectMetadata().also {
            it.contentType = fileContent.type.toString()
            it.contentLength = fileContent.size.toLong()
        }
        val putObjectRequest = PutObjectRequest(s3Properties.bucketName,
                                                convertToObjectName(fullPath),
                                                fileContent.content,
                                                objectMetadata)

        amazonS3.putObject(putObjectRequest)
    }

    override fun remove(fullPath: FullPath) {
        amazonS3.deleteObject(s3Properties.bucketName, convertToObjectName(fullPath))
    }

    private fun convertToObjectName(fullPath: FullPath): String = fullPath.path.substring(1)
}
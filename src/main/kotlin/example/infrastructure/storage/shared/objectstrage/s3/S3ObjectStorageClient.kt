package example.infrastructure.storage.shared.objectstrage.s3

import com.amazonaws.services.s3.model.*
import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("prod")
@Component
class S3ObjectStorageClient(private val s3Properties: S3Properties,
                            private val s3ClientProvider: S3ClientProvider) : ObjectStorageClient {
    override fun getRootPath(): String {
        return s3Properties.rootPath
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

        s3ClientProvider.getS3Client().putObject(putObjectRequest)
    }

    override fun remove(fullPath: FullPath) {
        s3ClientProvider.getS3Client().deleteObject(s3Properties.bucketName,
                                                    convertToObjectName(fullPath))
    }

    private fun convertToObjectName(fullPath: FullPath): String = fullPath.relativePath.substring(1)
}
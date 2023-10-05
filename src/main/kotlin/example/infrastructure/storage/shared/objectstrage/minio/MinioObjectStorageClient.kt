package example.infrastructure.storage.shared.objectstrage.minio

import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import io.minio.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("local")
@Component
class MinioObjectStorageClient(private val minioProperties: MinioProperties,
                               private val minioClient: MinioClient) : ObjectStorageClient {
    override fun getEndpoint(): String {
        return minioProperties.endpoint + "/" + minioProperties.bucketName
    }

    override fun put(fullPath: FullPath, fileContent: FileContent) {
        minioClient.putObject(PutObjectArgs.builder()
                                  .bucket(minioProperties.bucketName)
                                  .`object`(convertToObjectName(fullPath))
                                  .stream(fileContent.content,
                                          fileContent.size.toLong(),
                                          -1)
                                  .contentType(fileContent.type.toString())
                                  .build())
    }

    override fun remove(fullPath: FullPath) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                                     .bucket(minioProperties.bucketName)
                                     .`object`(convertToObjectName(fullPath))
                                     .build())
    }

    private fun convertToObjectName(fullPath: FullPath): String = fullPath.path.substring(1)
}
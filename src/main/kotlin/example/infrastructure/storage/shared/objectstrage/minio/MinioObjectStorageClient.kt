package example.infrastructure.storage.shared.objectstrage.minio

import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import io.minio.*
import org.springframework.stereotype.*

@Component
class MinioObjectStorageClient(private val minioProperties: MinioProperties,
                               private val minioClientProvider: MinioClientProvider) : ObjectStorageClient {
    override fun getRootPath(): String {
        return minioProperties.rootPath
    }

    override fun put(fullPath: FullPath, fileContent: FileContent) {
        minioClientProvider.getMinioClient().putObject(PutObjectArgs.builder()
                                                           .bucket(minioProperties.bucketName)
                                                           .`object`(convertToObjectName(fullPath))
                                                           .stream(fileContent.content,
                                                                   fileContent.size.toLong(),
                                                                   -1)
                                                           .contentType(fileContent.type.toString())
                                                           .build())
    }

    override fun remove(fullPath: FullPath) {
        minioClientProvider.getMinioClient().removeObject(RemoveObjectArgs.builder()
                                                              .bucket(minioProperties.bucketName)
                                                              .`object`(convertToObjectName(fullPath))
                                                              .build())
    }

    private fun convertToObjectName(fullPath: FullPath): String = fullPath.relativePath.substring(1)
}
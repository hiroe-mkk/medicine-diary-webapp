package example.infrastructure.storage.shared.objectstrage.minio

import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import io.minio.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("local", "dev")
@Component
class MinioObjectStorageClient(private val minioProperties: MinioProperties,
                               private val minioClient: MinioClient) : ObjectStorageClient {
    override fun getEndpoint(): String {
        return minioProperties.endpoint + "/" + minioProperties.bucketName
    }

    override fun put(url: URL, fileContent: FileContent) {
        minioClient.putObject(PutObjectArgs.builder()
                                  .bucket(minioProperties.bucketName)
                                  .`object`(convertToObjectName(url))
                                  .stream(fileContent.content,
                                          fileContent.size.toLong(),
                                          -1)
                                  .contentType(fileContent.type.toString())
                                  .build())
    }

    override fun copy(sourceURL: URL, targetURL: URL) {
        minioClient.copyObject(CopyObjectArgs.builder()
                                   .source(CopySource.builder()
                                               .bucket(minioProperties.bucketName)
                                               .`object`(convertToObjectName(sourceURL))
                                               .build())
                                   .bucket(minioProperties.bucketName)
                                   .`object`(convertToObjectName(targetURL))
                                   .build())
    }

    override fun remove(url: URL) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                                     .bucket(minioProperties.bucketName)
                                     .`object`(convertToObjectName(url))
                                     .build())
    }

    override fun removeAll(urls: Collection<URL>) {
        urls.forEach { remove(it) }
    }

    private fun convertToObjectName(URL: URL): String = URL.path.substring(1)
}
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

    override fun put(URL: URL, fileContent: FileContent) {
        minioClient.putObject(PutObjectArgs.builder()
                                  .bucket(minioProperties.bucketName)
                                  .`object`(convertToObjectName(URL))
                                  .stream(fileContent.content,
                                          fileContent.size.toLong(),
                                          -1)
                                  .contentType(fileContent.type.toString())
                                  .build())
    }

    override fun remove(URL: URL) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                                     .bucket(minioProperties.bucketName)
                                     .`object`(convertToObjectName(URL))
                                     .build())
    }

    override fun removeAll(urls: Collection<URL>) {
        urls.forEach { remove(it) }
    }

    private fun convertToObjectName(URL: URL): String = URL.path.substring(1)
}
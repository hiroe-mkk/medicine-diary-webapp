package example.infrastructure.storage.shared.objectstrage.minio

import example.domain.model.account.profile.profileimage.*
import example.domain.shared.type.*
import io.minio.*
import io.minio.errors.*
import net.bytebuddy.utility.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.context.properties.*
import org.springframework.boot.test.context.*
import org.springframework.http.*
import org.springframework.test.context.junit.jupiter.*
import java.io.*

@SpringJUnitConfig(initializers = [ConfigDataApplicationContextInitializer::class])
@EnableConfigurationProperties(MinioProperties::class)
internal class MinioObjectStorageClientTest(@Autowired private val minioProperties: MinioProperties) {
    private val minioClientProvider: MinioClientProvider = MinioClientProvider(minioProperties)
    private val minioObjectStorageClient: MinioObjectStorageClient = MinioObjectStorageClient(minioProperties,
                                                                                              minioClientProvider)

    private lateinit var fullPath: FullPath
    private lateinit var fileContent: FileContent

    @BeforeEach
    internal fun setUp() {
        fullPath = ProfileImageFullPath(minioProperties.rootPath, "/files/testFile")
        val byteArray = RandomString(10).nextString().toByteArray()
        fileContent = FileContent(MediaType.TEXT_PLAIN,
                                  byteArray.size,
                                  ByteArrayInputStream(byteArray))
    }

    @AfterEach
    internal fun tearDown() {
        minioObjectStorageClient.remove(fullPath)
    }

    @Test
    fun afterPutObject_existsInMinIO() {
        //when:
        minioObjectStorageClient.put(fullPath, fileContent)

        //then:
        assertThat(getObject(fullPath)).hasSameContentAs(fileContent.content)
    }

    private fun getObject(path: FullPath): InputStream {
        val response = minioClientProvider.getMinioClient()
            .getObject(GetObjectArgs.builder()
                           .bucket(minioProperties.bucketName)
                           .`object`(path.relativePath.substring(1))
                           .build())
        response.readAllBytes()
        return response
    }
}
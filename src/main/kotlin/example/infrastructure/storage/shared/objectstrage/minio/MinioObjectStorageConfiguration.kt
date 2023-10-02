package example.infrastructure.storage.shared.objectstrage.minio

import example.infrastructure.storage.shared.objectstrage.s3.*
import org.springframework.boot.context.properties.*
import org.springframework.context.annotation.*

@Profile("local")
@Configuration
@EnableConfigurationProperties(MinioProperties::class)
class MinioObjectStorageConfiguration
package example.infrastructure.storage.shared.objectstrage.s3

import com.amazonaws.auth.*
import com.amazonaws.services.s3.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("prod")
@Component
class S3ClientProvider {
    fun getS3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.defaultClient()
    }
}
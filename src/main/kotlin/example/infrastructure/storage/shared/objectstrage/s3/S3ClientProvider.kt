package example.infrastructure.storage.shared.objectstrage.s3

import com.amazonaws.auth.*
import com.amazonaws.services.s3.*
import org.springframework.context.annotation.*
import org.springframework.stereotype.*

@Profile("prod")
@Component
class S3ClientProvider(private val s3Properties: S3Properties) {
    fun getS3Client(): AmazonS3 {
        val credentials = BasicAWSCredentials(s3Properties.accessKey,
                                              s3Properties.secretKey)
        return AmazonS3ClientBuilder.standard()
            .withCredentials(AWSStaticCredentialsProvider(credentials))
            .withRegion(s3Properties.region)
            .build()
    }
}
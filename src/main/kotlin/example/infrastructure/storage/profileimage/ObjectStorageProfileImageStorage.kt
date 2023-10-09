package example.infrastructure.storage.profileimage

import example.domain.model.account.profile.profileimage.*
import example.domain.shared.type.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.stereotype.*
import java.util.*

@Component
class ObjectStorageProfileImageStorage(private val objectStorageClient: ObjectStorageClient) : ProfileImageStorage {
    override fun createURL(): ProfileImageURL {
        val path = "/profileimage/${UUID.randomUUID()}"
        return ProfileImageURL(objectStorageClient.getEndpoint(), path)
    }

    override fun upload(profileImageURL: ProfileImageURL, fileContent: FileContent) {
        objectStorageClient.put(profileImageURL, fileContent)
    }

    override fun delete(profileImageURL: ProfileImageURL) {
        objectStorageClient.remove(profileImageURL)
    }
}
package example.infrastructure.storage.profileimage

import example.domain.model.account.profile.profileimage.*
import example.infrastructure.storage.shared.objectstrage.*
import org.springframework.stereotype.*
import java.util.*

@Component
class ObjectStorageProfileImageStorage(private val objectStorageClient: ObjectStorageClient) : ProfileImageStorage {
    override fun createPath(): ProfileImageFullPath {
        val relativePath = "/profileimage/${UUID.randomUUID()}"
        return ProfileImageFullPath(objectStorageClient.getRootPath(), relativePath)
    }

    override fun upload(profileImage: ProfileImage) {
        objectStorageClient.put(profileImage.path, profileImage.fileContent)
    }

    override fun delete(profileImageFullPath: ProfileImageFullPath) {
        objectStorageClient.remove(profileImageFullPath)
    }
}
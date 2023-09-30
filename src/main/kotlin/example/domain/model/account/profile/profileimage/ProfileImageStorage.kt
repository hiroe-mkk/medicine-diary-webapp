package example.domain.model.account.profile.profileimage

interface ProfileImageStorage {
    fun createPath(): ProfileImageFullPath

    fun upload(profileImage: ProfileImage)

    fun delete(profileImageFullPath: ProfileImageFullPath)
}
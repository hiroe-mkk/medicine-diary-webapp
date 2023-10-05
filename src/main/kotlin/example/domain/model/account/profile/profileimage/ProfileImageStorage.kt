package example.domain.model.account.profile.profileimage

interface ProfileImageStorage {
    fun createURL(): ProfileImageURL

    fun upload(profileImage: ProfileImage)

    fun delete(profileImageURL: ProfileImageURL)
}
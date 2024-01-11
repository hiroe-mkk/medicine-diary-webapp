package example.domain.model.account.profile.profileimage

import example.domain.shared.type.*

interface ProfileImageStorage {
    fun createURL(): ProfileImageURL

    fun upload(profileImageURL: ProfileImageURL, fileContent: FileContent)

    fun delete(profileImageURL: ProfileImageURL)
}
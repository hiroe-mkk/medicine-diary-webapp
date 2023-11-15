package example.infrastructure.storage.shared.objectstrage

import example.domain.shared.type.*

interface ObjectStorageClient {
    fun getEndpoint(): String

    fun put(URL: URL, fileContent: FileContent)

    fun remove(URL: URL)

    fun removeAll(urls: Collection<URL>)
}
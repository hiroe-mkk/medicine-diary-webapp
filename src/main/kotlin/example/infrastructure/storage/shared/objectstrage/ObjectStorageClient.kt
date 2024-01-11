package example.infrastructure.storage.shared.objectstrage

import example.domain.shared.type.*

interface ObjectStorageClient {
    fun getEndpoint(): String

    fun put(url: URL, fileContent: FileContent)

    fun copy(sourceURL: URL, targetURL: URL)

    fun remove(url: URL)

    fun removeAll(urls: Collection<URL>)
}
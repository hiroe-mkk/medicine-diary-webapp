package example.infrastructure.storage.shared.objectstrage

import example.domain.shared.type.*

interface ObjectStorageClient {
    fun getRootPath(): String

    fun put(fullPath: FullPath, fileContent: FileContent)

    fun remove(fullPath: FullPath)
}
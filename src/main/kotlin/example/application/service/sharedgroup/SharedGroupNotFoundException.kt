
import example.domain.model.sharedgroup.*
import example.domain.shared.exception.*

/**
 * 共有グループが見つからなかったことを示す例外
 */
class SharedGroupNotFoundException(val sharedGroupId: SharedGroupId)
    : ResourceNotFoundException("共有グループが見つかりませんでした。", sharedGroupId)
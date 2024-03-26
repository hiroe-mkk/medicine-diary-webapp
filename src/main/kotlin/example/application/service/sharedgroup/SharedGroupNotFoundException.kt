package example.application.service.sharedgroup

import example.application.shared.exception.*
import example.domain.model.sharedgroup.*

/**
 * 共有グループが見つからなかったことを示す例外
 */
class SharedGroupNotFoundException(val sharedGroupId: SharedGroupId)
    : ResourceNotFoundException("共有グループが見つかりませんでした。", sharedGroupId)
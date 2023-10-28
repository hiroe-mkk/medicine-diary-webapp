package example.presentation.controller.api.user

import com.fasterxml.jackson.annotation.*
import example.application.query.shared.type.*

class JSONUsersResponse(val users: List<JSONUser>) {
    companion object {
        fun from(users: List<User>): JSONUsersResponse {
            return JSONUsersResponse(users.map { JSONUser.from(it) })
        }
    }

    class JSONUser(val accountId: String,
                   val username: String,
                   @JsonInclude(JsonInclude.Include.NON_NULL)
                   val profileImageURL: String?) {
        companion object {
            fun from(user: User): JSONUser {
                return JSONUser(user.accountId.value,
                                user.username.value,
                                user.profileImageURL?.toURL())
            }
        }
    }
}
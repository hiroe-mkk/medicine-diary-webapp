package example.application.query.user

import com.fasterxml.jackson.annotation.*

data class JSONUser(val accountId: String,
                    val username: String,
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    val profileImageURL: String?)
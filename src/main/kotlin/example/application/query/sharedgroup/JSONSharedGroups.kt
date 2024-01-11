package example.application.query.sharedgroup

import com.fasterxml.jackson.annotation.*

data class JSONSharedGroups(@JsonInclude(JsonInclude.Include.NON_NULL)
                            val participatingSharedGroup: JSONSharedGroup?,
                            val invitedSharedGroups: Set<JSONSharedGroup>)
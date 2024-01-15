package example.application.query.sharedgroup

import com.fasterxml.jackson.annotation.*

class JSONSharedGroups(@JsonInclude(JsonInclude.Include.NON_NULL)
                       val participatingSharedGroup: JSONSharedGroup?,
                       val invitedSharedGroups: Set<JSONSharedGroup>)
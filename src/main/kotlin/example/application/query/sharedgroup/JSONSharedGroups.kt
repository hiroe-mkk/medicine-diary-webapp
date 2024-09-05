package example.application.query.sharedgroup

import com.fasterxml.jackson.annotation.*

class JSONSharedGroups(@JsonInclude(JsonInclude.Include.NON_NULL)
                       val joinedSharedGroup: JSONSharedGroup?,
                       val invitedSharedGroups: Set<JSONSharedGroup>)

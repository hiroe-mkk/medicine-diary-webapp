package example.application.query.sharedgroup

data class JSONSharedGroups(val participatingSharedGroup: JSONSharedGroup?,
                            val invitedSharedGroups: Set<JSONSharedGroup>)
package example.application.query.sharedgroup

data class SharedGroups(val participatingSharedGroup: SharedGroupDetail?,
                        val invitedSharedGroups: Set<SharedGroupDetail>)
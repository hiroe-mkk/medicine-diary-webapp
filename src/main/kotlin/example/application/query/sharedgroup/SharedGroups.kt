package example.application.query.sharedgroup

data class SharedGroups(val participatingSharedGroup: DisplaySharedGroup?,
                        val invitedSharedGroups: Set<DisplaySharedGroup>)
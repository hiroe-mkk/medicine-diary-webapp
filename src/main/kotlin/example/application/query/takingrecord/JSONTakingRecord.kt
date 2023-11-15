package example.application.query.takingrecord

import example.application.query.user.*

data class JSONTakingRecord(val takingRecordId: String,
                            val takenMedicine: JSONTakenMedicine,
                            val followUp: JSONFollowUp,
                            val note: String,
                            val takenAt: String,
                            val recorder: JSONUser,
                            val isRecordedBySelf: Boolean)
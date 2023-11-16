package example.application.query.medicationrecord

import example.application.query.user.*

data class JSONMedicationRecord(val medicationRecordId: String,
                                val takenMedicine: JSONTakenMedicine,
                                val followUp: JSONFollowUp,
                                val note: String,
                                val takenAt: String,
                                val recorder: JSONUser,
                                val isRecordedBySelf: Boolean)
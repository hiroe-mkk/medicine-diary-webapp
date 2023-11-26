package example.infrastructure.repository.medicationrecord

import example.domain.model.medicationrecord.*
import java.time.*

class MedicationRecordSaveEntity(val medicationRecordId: String,
                                 val recorder: String,
                                 val takenMedicine: String,
                                 val quantity: Double,
                                 val symptom: String,
                                 val beforeMedication: ConditionLevel,
                                 val afterMedication: ConditionLevel?,
                                 val note: String,
                                 val takenAt: LocalDateTime)
package example.testhelper.factory

import example.application.service.medicationrecord.*
import example.domain.model.medicationrecord.*
import java.time.*

object TestMedicationRecordEditCommandFactory {
    fun createCompletedAdditionCommand(takenMedicine: String = "testMedicineId",
                                       quantity: Double? = 1.0,
                                       symptom: String = "頭痛",
                                       beforeMedication: ConditionLevel = ConditionLevel.A_LITTLE_BAD,
                                       afterMedication: ConditionLevel? = null,
                                       note: String = "",
                                       takenAt: LocalDateTime =
                                               LocalDateTime.of(2020, 1, 1, 7, 0)): MedicationRecordEditCommand {
        return MedicationRecordEditCommand(takenMedicine,
                                           quantity,
                                           symptom,
                                           beforeMedication,
                                           afterMedication,
                                           note,
                                           takenAt)
    }

    fun createCompletedModificationCommand(takenMedicine: String = "testMedicineId",
                                           quantity: Double? = 2.0,
                                           symptom: String = "頭痛",
                                           beforeMedication: ConditionLevel = ConditionLevel.A_LITTLE_BAD,
                                           afterMedication: ConditionLevel? = ConditionLevel.GOOD,
                                           note: String = "それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。",
                                           takenAt: LocalDateTime =
                                                   LocalDateTime.of(2020, 1, 1, 10, 0)): MedicationRecordEditCommand {
        return MedicationRecordEditCommand(takenMedicine,
                                           quantity,
                                           symptom,
                                           beforeMedication,
                                           afterMedication,
                                           note,
                                           takenAt)
    }
}
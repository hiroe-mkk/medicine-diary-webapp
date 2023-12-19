package example.testhelper.factory

import example.application.service.medicationrecord.*
import example.domain.model.medicationrecord.*
import java.time.*

object TestMedicationRecordFactory {
    fun createCompletedAdditionCommand(takenMedicine: String = "testMedicineId",
                                       quantity: Double? = 1.0,
                                       symptom: String = "頭痛",
                                       beforeMedication: ConditionLevel = ConditionLevel.A_LITTLE_BAD,
                                       afterMedication: ConditionLevel? = null,
                                       note: String = "",
                                       takenMedicineOn: LocalDate = LocalDate.of(2020, 1, 1),
                                       takenMedicineAt: LocalTime = LocalTime.of(7, 0),
                                       symptomOnsetAt: LocalTime? = LocalTime.of(6, 30),
                                       onsetEffectAt: LocalTime? = null)
            : MedicationRecordEditCommand {
        return MedicationRecordEditCommand(takenMedicine,
                                           quantity,
                                           symptom,
                                           beforeMedication,
                                           afterMedication,
                                           note,
                                           takenMedicineOn,
                                           takenMedicineAt,
                                           symptomOnsetAt,
                                           onsetEffectAt)
    }

    fun createCompletedModificationCommand(takenMedicine: String = "testMedicineId",
                                           quantity: Double? = 2.0,
                                           symptom: String = "頭痛",
                                           beforeMedication: ConditionLevel = ConditionLevel.A_LITTLE_BAD,
                                           afterMedication: ConditionLevel? = ConditionLevel.GOOD,
                                           note: String = "それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。",
                                           takenMedicineOn: LocalDate = LocalDate.of(2020, 1, 2),
                                           takenMedicineAt: LocalTime = LocalTime.of(10, 0),
                                           symptomOnsetAt: LocalTime? = null,
                                           onsetEffectAt: LocalTime? = LocalTime.of(10, 30))
            : MedicationRecordEditCommand {
        return MedicationRecordEditCommand(takenMedicine,
                                           quantity,
                                           symptom,
                                           beforeMedication,
                                           afterMedication,
                                           note,
                                           takenMedicineOn,
                                           takenMedicineAt,
                                           symptomOnsetAt,
                                           onsetEffectAt)
    }
}
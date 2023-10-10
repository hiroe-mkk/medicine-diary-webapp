package example.testhelper.factory

import example.application.service.takingrecord.*
import example.application.service.takingrecord.TakingRecordEditCommand.*
import example.domain.model.takingrecord.*
import java.time.*

object TestTakingRecordEditCommandFactory {
    fun createCompletedAdditionCommand(takenMedicine: String = "testMedicineId",
                                       quantity: Double? = 1.0,
                                       symptoms: List<FollowUpInputField> =
                                               listOf(FollowUpInputField("頭痛",
                                                                         ConditionLevel.A_LITTLE_BAD,
                                                                         null)),
                                       note: String = "",
                                       takenAt: LocalDateTime =
                                               LocalDateTime.of(2020, 1, 1, 7, 0)): TakingRecordEditCommand {
        return TakingRecordEditCommand(takenMedicine, quantity, symptoms, note, takenAt)
    }

    fun createCompletedModificationCommand(takenMedicine: String = "testMedicineId",
                                           quantity: Double? = 2.0,
                                           symptoms: List<FollowUpInputField> =
                                                   listOf(FollowUpInputField("頭痛",
                                                                             ConditionLevel.A_LITTLE_BAD,
                                                                             ConditionLevel.GOOD)),
                                           note: String = "それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。",
                                           takenAt: LocalDateTime =
                                                   LocalDateTime.of(2020, 1, 1, 10, 0)): TakingRecordEditCommand {
        return TakingRecordEditCommand(takenMedicine, quantity, symptoms, note, takenAt)
    }
}
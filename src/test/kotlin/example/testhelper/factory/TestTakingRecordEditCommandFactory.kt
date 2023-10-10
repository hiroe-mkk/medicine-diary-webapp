package example.testhelper.factory

import example.application.service.takingrecord.*
import example.application.service.takingrecord.TakingRecordEditCommand.*
import example.domain.model.takingrecord.*

object TestTakingRecordEditCommandFactory {
    fun createCompletedAdditionCommand(medicineId: String = "testMedicineId",
                                       quantity: Double? = 1.0,
                                       symptoms: List<FollowUpInputField> =
                                               listOf(FollowUpInputField("頭痛",
                                                                         ConditionLevel.A_LITTLE_BAD,
                                                                         null)),
                                       note: String = ""): TakingRecordEditCommand {
        return TakingRecordEditCommand(medicineId, quantity, symptoms, note)
    }
}
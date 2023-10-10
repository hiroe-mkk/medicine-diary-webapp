package example.testhelper.factory

import example.application.service.takingrecord.*
import example.application.service.takingrecord.TakingRecordEditCommand.*
import example.domain.model.takingrecord.*

object TestTakingRecordFactory {
    fun createTakingRecordEditCommand(medicineId: String = "testMedicineId",
                                      quantity: Double? = 1.0,
                                      symptoms: List<FollowUpInputField> =
                                              listOf(FollowUpInputField("頭痛",
                                                                        ConditionLevel.A_LITTLE_BAD,
                                                                        ConditionLevel.GOOD)),
                                      note: String = "それほど酷い頭痛ではなかったけれど、早めに飲んでおいたらいつもより早めに治った気がする。"): TakingRecordEditCommand {
        return TakingRecordEditCommand(medicineId, quantity, symptoms, note)
    }
}
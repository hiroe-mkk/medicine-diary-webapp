package example.domain.model.medicationrecord

/**
 * 経過観察
 */
data class FollowUp(val symptom: String,
                    val beforeMedication: ConditionLevel,
                    val afterMedication: ConditionLevel?) {
    override fun toString(): String {
        return if (afterMedication == null) {
            "$symptom ( 服用前：${beforeMedication.str} )"

        } else {
            "$symptom ( 服用前：${beforeMedication.str} / 服用後：${afterMedication.str} )"
        }
    }
}
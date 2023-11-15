package example.domain.model.medicationrecord

/**
 * 経過観察
 */
data class FollowUp(val symptom: String,
                    val beforeTaking: ConditionLevel,
                    val afterTaking: ConditionLevel?) {
    override fun toString(): String {
        return if (afterTaking == null) {
            "$symptom ( 服用前：${beforeTaking.str} )"

        } else {
            "$symptom ( 服用前：${beforeTaking.str} / 服用後：${afterTaking.str} )"
        }
    }
}
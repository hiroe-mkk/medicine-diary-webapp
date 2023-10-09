package example.domain.model.takingrecord

/**
 * 体調レベル
 */
enum class ConditionLevel(val level: Int, val str: String) {
    GOOD(1, "良い"),
    NOT_BAD(2, "普通"),
    A_LITTLE_BAD(3, "少し悪い"),
    BAD(4, "悪い"),
    VERY_BAD(5, "とても悪い");
}
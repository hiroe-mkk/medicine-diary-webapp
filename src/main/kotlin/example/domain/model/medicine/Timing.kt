package example.domain.model.medicine

/**
 * 服薬するタイミング
 */
enum class Timing(val str: String) {
    IN_THE_MORNING("朝"),
    IN_THE_AFTERNOON("昼"),
    IN_THE_EVENING("晩"),
    AT_BEDTIME("就寝前"),
    RIGHT_AFTER_WAKING_UP("起床時"),
    BEFORE_MEAL("食前"),
    AFTER_MEAL("食後"),
    BETWEEN_MEAL("食間"),
    RIGHT_BEFORE_MEAL("食直前"),
    RIGHT_AFTER_MEAL("食直後"),
    AS_NEEDED("頓服");
}
package example.domain.model.medicine

/**
 * 用法 (1日当たりの服薬回数と服薬するタイミング)
 */
data class Administration(val timesPerDay: Int,
                          val timingOptions: List<Timing>) {
    override fun toString(): String {
        if (timingOptions.isEmpty()) return "1日 ${timesPerDay}回"

        val timingOptionsStr = timingOptions.joinToString(transform = { it.str },
                                                          separator = "、",
                                                          prefix = "( ", postfix = " )")
        return "1日 ${timesPerDay}回 $timingOptionsStr"
    }
}
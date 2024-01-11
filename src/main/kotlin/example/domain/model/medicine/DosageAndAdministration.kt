package example.domain.model.medicine

/**
 * 用法・用量
 */
data class DosageAndAdministration(val dose: Dose,
                                   val doseUnit: String,
                                   val timesPerDay: Int,
                                   val timingOptions: List<Timing>) {

    override fun toString(): String {
        val dosage = "1回 ${dose}${doseUnit} 1日 ${timesPerDay}回"
        if (timingOptions.isEmpty()) return dosage

        val timingOptionsStr = timingOptions.joinToString(transform = { it.str },
                                                          separator = "、",
                                                          prefix = " ( ", postfix = " ) ")
        return dosage + timingOptionsStr
    }
}
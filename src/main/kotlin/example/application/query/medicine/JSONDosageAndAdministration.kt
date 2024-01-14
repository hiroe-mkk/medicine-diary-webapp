package example.application.query.medicine

import example.domain.model.medicine.*

class JSONDosageAndAdministration(val quantity: String,
                                  val doseUnit: String,
                                  val timesPerDay: String) {
    private lateinit var timingOptionsForMapping: List<Timing>
    val timingOptions: List<String>
        get() = timingOptionsForMapping.map { it.str }
}
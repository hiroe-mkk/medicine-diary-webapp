package example.domain.model.medicine

import example.domain.model.account.*
import java.time.*

/**
 * 薬
 */
class Medicine(val id: MedicineId,
               val accountId: AccountId,
               val name: String,
               val takingUnit: String,
               val dosage: Dosage,
               val administration: Administration,
               val effects: Effects,
               val precautions: String,
               val registeredAt: LocalDateTime)
package example.application.query.takingrecord

import example.domain.model.account.*
import example.domain.model.account.profile.*
import example.domain.model.account.profile.profileimage.*
import example.domain.model.medicine.*
import example.domain.model.takingrecord.*
import java.time.*

data class TakingRecordOverview(val takingRecordId: TakingRecordId,
                                val beforeTaking: ConditionLevel,
                                val afterTaking: ConditionLevel?,
                                val takenAt: LocalDateTime,
                                val medicineId: MedicineId,
                                val medicineName: String,
                                val accountId: AccountId,
                                val username: Username,
                                val profileImageURL: ProfileImageURL?)
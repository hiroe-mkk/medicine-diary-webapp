package example.infrastructure.db.query.medicine

import example.application.query.medicine.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONMedicineOverviewsQueryService(private val jsonMedicineOverviewsMapper: JSONMedicineOverviewsMapper)
    : JSONMedicineOverviewsQueryService {
    override fun getMedicineOverviews(filter: MedicineFilter?,
                                      userSession: UserSession): JSONOwnerBaseMedicineOverviews {
        val accountId = userSession.accountId.toString()
        val ownedMedicines = jsonMedicineOverviewsMapper.findAllOwnedMedicineOverviews(accountId,
                                                                                       filter?.nonBlankEffect)
        val sharedGroupMedicines = jsonMedicineOverviewsMapper.findAllSharedGroupMedicineOverviews(accountId,
                                                                                                   filter?.nonBlankEffect)
        val membersMedicines = jsonMedicineOverviewsMapper.findAllMembersMedicineOverviews(accountId,
                                                                                           filter?.nonBlankEffect)
        return JSONOwnerBaseMedicineOverviews(ownedMedicines, sharedGroupMedicines, membersMedicines)
    }

    override fun getAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews {
        val accountId = userSession.accountId.toString()
        val availableMedicines = jsonMedicineOverviewsMapper.findAllAvailableMedicineOverviews(accountId)
        return JSONMedicineOverviews(availableMedicines)
    }
}

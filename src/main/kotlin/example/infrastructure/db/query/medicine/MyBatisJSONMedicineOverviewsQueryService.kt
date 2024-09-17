package example.infrastructure.db.query.medicine

import example.application.query.medicine.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONMedicineOverviewsQueryService(private val jsonMedicineOverviewsMapper: JSONMedicineOverviewsMapper)
    : JSONMedicineOverviewsQueryService {
    // TODO: 3回フェッチしているため、より効率の良い方法に変更する
    override fun getMedicineOverviews(filter: MedicineFilter?,
                                      userSession: UserSession): JSONOwnerBaseMedicineOverviews {
        val accountId = userSession.accountId.toString()
        val ownedMedicines = jsonMedicineOverviewsMapper.findAllOwnedMedicineOverviews(accountId,
                                                                                       filter?.effect)
        val sharedGroupMedicines = jsonMedicineOverviewsMapper.findAllSharedGroupMedicineOverviews(accountId,
                                                                                                   filter?.effect)
        val membersMedicines = jsonMedicineOverviewsMapper.findAllMembersMedicineOverviews(accountId,
                                                                                           filter?.effect)
        return JSONOwnerBaseMedicineOverviews(ownedMedicines, sharedGroupMedicines, membersMedicines)
    }

    override fun getAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews {
        val accountId = userSession.accountId.toString()
        val availableMedicines = jsonMedicineOverviewsMapper.findAllAvailableMedicineOverviews(accountId)
        return JSONMedicineOverviews(availableMedicines)
    }
}

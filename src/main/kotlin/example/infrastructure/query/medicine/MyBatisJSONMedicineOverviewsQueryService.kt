package example.infrastructure.query.medicine

import example.application.query.medicine.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONMedicineOverviewsQueryService(private val jsonMedicineOverviewsMapper: JsonMedicineOverviewsMapper)
    : JSONMedicineOverviewsQueryService {
    override fun findMedicineOverviews(userSession: UserSession,
                                       medicineFilter: MedicineFilter?): JSONOwnerBaseMedicineOverviews {
        // TODO: 3回フェッチしているため、より効率の良い方法に変更する
        val accountId = userSession.accountId.toString()
        val ownedMedicines = jsonMedicineOverviewsMapper.findAllOwnedMedicineOverviews(accountId,
                                                                                       medicineFilter?.effect)
        val sharedGroupMedicines = jsonMedicineOverviewsMapper.findAllSharedGroupMedicineOverviews(accountId,
                                                                                                   medicineFilter?.effect)
        val membersMedicines = jsonMedicineOverviewsMapper.findAllMembersMedicineOverviews(accountId,
                                                                                           medicineFilter?.effect)
        return JSONOwnerBaseMedicineOverviews(ownedMedicines, sharedGroupMedicines, membersMedicines)
    }

    override fun findJSONAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews {
        val accountId = userSession.accountId.toString()
        val availableMedicines = jsonMedicineOverviewsMapper.findAllAvailableMedicineOverviews(accountId)
        return JSONMedicineOverviews(availableMedicines)
    }
}
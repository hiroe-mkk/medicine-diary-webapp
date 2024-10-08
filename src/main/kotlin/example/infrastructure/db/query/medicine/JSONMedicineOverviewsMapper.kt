package example.infrastructure.db.query.medicine

import example.application.query.medicine.*
import org.apache.ibatis.annotations.*

@Mapper
interface JSONMedicineOverviewsMapper {
    fun findAllAvailableMedicineOverviews(accountId: String): List<JSONMedicineOverview>

    fun findAllOwnedMedicineOverviews(accountId: String, effect: String?): List<JSONMedicineOverview>

    fun findAllSharedGroupMedicineOverviews(accountId: String, effect: String?): List<JSONMedicineOverview>

    fun findAllMembersMedicineOverviews(accountId: String, effect: String?): List<JSONMedicineOverview>
}

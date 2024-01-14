package example.infrastructure.query.medicine

import example.application.query.medicine.*
import example.application.shared.usersession.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Component
@Transactional(readOnly = true)
class MyBatisJSONMedicineOverviewsQueryService(private val jsonMedicineOverviewsMapper: JsonMedicineOverviewsMapper)
    : JSONMedicineOverviewsQueryService {
    override fun findJSONAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews {
        val result = jsonMedicineOverviewsMapper.findAllAvailableMedicineOverviews(userSession.accountId.toString())
        return JSONMedicineOverviews(result)
    }
}
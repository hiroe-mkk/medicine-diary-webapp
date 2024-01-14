package example.application.query.medicine

import example.application.shared.usersession.*

interface JSONMedicineOverviewsQueryService {
    /**
     * 服用可能な薬概要一覧を取得する
     */
    fun findJSONAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews
}
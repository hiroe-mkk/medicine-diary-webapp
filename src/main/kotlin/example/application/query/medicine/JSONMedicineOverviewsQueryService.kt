package example.application.query.medicine

import example.application.shared.usersession.*

interface JSONMedicineOverviewsQueryService {
    /**
     * 薬概要一覧を取得する
     */
    fun getMedicineOverviews(filter: MedicineFilter?, userSession: UserSession): JSONOwnerBaseMedicineOverviews

    /**
     * 服用可能な薬概要一覧を取得する
     */
    fun getAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews
}

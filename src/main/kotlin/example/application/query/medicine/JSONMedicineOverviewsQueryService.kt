package example.application.query.medicine

import example.application.shared.usersession.*

interface JSONMedicineOverviewsQueryService {
    /**
     * 薬概要一覧を取得する
     */
    fun findMedicineOverviews(userSession: UserSession, medicineFilter: MedicineFilter?): JSONOwnerBaseMedicineOverviews

    /**
     * 服用可能な薬概要一覧を取得する
     */
    fun findJSONAvailableMedicineOverviews(userSession: UserSession): JSONMedicineOverviews
}
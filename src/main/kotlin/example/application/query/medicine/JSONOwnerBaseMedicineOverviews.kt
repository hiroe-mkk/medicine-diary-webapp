package example.application.query.medicine

class JSONOwnerBaseMedicineOverviews(val ownedMedicines: List<JSONMedicineOverview>,
                                     val sharedGroupMedicines: List<JSONMedicineOverview>,
                                     val membersMedicines: List<JSONMedicineOverview>)
package example.application.service.medicine

data class MedicineOverviews(val ownedMedicines: List<MedicineOverviewDto>,
                             val sharedGroupMedicines: List<MedicineOverviewDto>,
                             val membersMedicines: List<MedicineOverviewDto>)
package example.application.service.medicineimage

import example.application.service.medicine.*
import example.application.shared.command.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineimage.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineImageService(private val medicineRepository: MedicineRepository,
                           private val medicineImageStorage: MedicineImageStorage,
                           private val medicineQueryService: MedicineQueryService) {
    /**
     * 薬画像を変更する
     */
    fun changeMedicineImage(medicineId: MedicineId,
                            command: ImageUploadCommand,
                            userSession: UserSession): MedicineImageURL {
        val medicine = findAvailableMedicineOrElseThrowException(medicineId, userSession)

        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }

        val medicineImageURL = medicineImageStorage.createURL()
        medicine.changeMedicineImage(medicineImageURL)
        medicineRepository.save(medicine)
        medicineImageStorage.upload(medicineImageURL, command.validatedFileContent())

        return medicineImageURL
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineQueryService.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}
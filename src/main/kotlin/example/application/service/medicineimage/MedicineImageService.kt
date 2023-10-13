package example.application.service.medicineimage

import example.application.service.medicine.*
import example.application.shared.command.*
import example.application.shared.usersession.*
import example.domain.model.medicine.*
import example.domain.model.medicine.medicineImage.*
import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
@Transactional
class MedicineImageService(private val medicineRepository: MedicineRepository,
                           private val medicineImageStorage: MedicineImageStorage) {
    /**
     * 薬画像を変更する
     */
    fun changeMedicineImage(medicineId: MedicineId,
                            command: ImageUploadCommand,
                            userSession: UserSession): MedicineImageURL {
        val medicine = findMedicineOrElseThrowException(medicineId, userSession)

        medicine.medicineImageURL?.let { medicineImageStorage.delete(it) }

        val medicineImageURL = medicineImageStorage.createURL()
        medicine.changeMedicineImage(medicineImageURL)
        medicineRepository.save(medicine)
        medicineImageStorage.upload(medicineImageURL, command.validatedFileContent())

        return medicineImageURL
    }

    private fun findMedicineOrElseThrowException(medicineId: MedicineId,
                                                 userSession: UserSession): Medicine {
        return findMedicineOwnedBy(medicineId, userSession) ?: throw MedicineNotFoundException(medicineId)
    }

    private fun findMedicineOwnedBy(medicineId: MedicineId, userSession: UserSession): Medicine? {
        val medicine = medicineRepository.findById(medicineId) ?: return null
        return if (medicine.isOwnedBy(userSession.accountId)) medicine else null
    }
}
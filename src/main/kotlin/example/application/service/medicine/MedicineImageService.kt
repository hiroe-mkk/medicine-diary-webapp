package example.application.service.medicine

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
                           private val medicineFinder: MedicineFinder) {
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

    /**
     * 薬画像を削除する
     */
    fun deleteMedicineImage(medicineId: MedicineId, userSession: UserSession) {
        val medicine = medicineFinder.findAvailableMedicine(medicineId, userSession.accountId) ?: return
        if (medicine.medicineImageURL == null) return

        medicineImageStorage.delete(medicine.medicineImageURL!!)
        medicine.deleteMedicineImage()
        medicineRepository.save(medicine)
    }

    private fun findAvailableMedicineOrElseThrowException(medicineId: MedicineId,
                                                          userSession: UserSession): Medicine {
        return medicineFinder.findAvailableMedicine(medicineId, userSession.accountId)
               ?: throw MedicineNotFoundException(medicineId)
    }
}

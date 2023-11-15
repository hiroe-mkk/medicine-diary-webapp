package example.application.service.takingrecord

import com.ninjasquad.springmockk.*
import example.application.query.takingrecord.*
import example.application.service.medicine.*
import example.application.shared.usersession.*
import example.domain.model.account.profile.*
import example.domain.model.medicine.*
import example.domain.model.sharedgroup.*
import example.domain.model.takingrecord.*
import example.domain.shared.exception.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import io.mockk.*
import io.mockk.impl.annotations.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import java.time.*

@DomainLayerTest
internal class TakingRecordServiceTest(@Autowired private val takingRecordRepository: TakingRecordRepository,
                                       @Autowired private val medicineQueryService: MedicineQueryService,
                                       @Autowired private val takingRecordQueryService: TakingRecordQueryService,
                                       @Autowired private val testAccountInserter: TestAccountInserter,
                                       @Autowired private val testMedicineInserter: TestMedicineInserter,
                                       @Autowired private val testTakingRecordInserter: TestTakingRecordInserter) {
    private val takingRecordService: TakingRecordService =
            TakingRecordService(takingRecordRepository, takingRecordQueryService, medicineQueryService)

    private lateinit var userSession: UserSession
    private lateinit var requesterMedicine: Medicine

    @BeforeEach
    internal fun setUp() {
        val requesterAccountId = testAccountInserter.insertAccountAndProfile().first.id
        userSession = UserSessionFactory.create(requesterAccountId)
        requesterMedicine = testMedicineInserter.insert(MedicineOwner.create(requesterAccountId))
    }

    @Nested
    inner class AddTakingRecordTest {
        @Test
        @DisplayName("服用記録を追加する")
        fun addTakingRecord() {
            //given:
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedAdditionCommand(takenMedicine = requesterMedicine.id.value)

            //when:
            val newTakingRecordId = takingRecordService.addTakingRecord(command, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(newTakingRecordId)
            val expected = TakingRecord.reconstruct(
                    newTakingRecordId,
                    userSession.accountId,
                    requesterMedicine.id,
                    command.validatedDose,
                    command.validFollowUp,
                    command.validatedNote,
                    command.validatedTakenAt)
            assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の追加に失敗する")
        fun medicineNotFound_addingTakingRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedAdditionCommand(takenMedicine = badMedicineId.value)

            //when:
            val target: () -> Unit = { takingRecordService.addTakingRecord(command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }

    @Nested
    inner class ModifyTakingRecordTest {
        private lateinit var takingRecord: TakingRecord
        private lateinit var command: TakingRecordEditCommand

        @BeforeEach
        internal fun setUp() {
            takingRecord = testTakingRecordInserter.insert(userSession.accountId, requesterMedicine.id)
            val newTakenMedicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId)).id.value
            command = TestTakingRecordEditCommandFactory.createCompletedModificationCommand(newTakenMedicine)
        }

        @Test
        @DisplayName("服用記録を修正する")
        fun modifyTakingRecord() {
            //when:
            takingRecordService.modifyTakingRecord(takingRecord.id, command, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
            val expected = TakingRecord.reconstruct(
                    takingRecord.id,
                    userSession.accountId,
                    command.validatedTakenMedicine,
                    command.validatedDose,
                    command.validFollowUp,
                    command.validatedNote,
                    command.validatedTakenAt)
            assertThat(foundTakingRecord).usingRecursiveComparison().isEqualTo(expected)
        }

        @Test
        @DisplayName("服用記録が見つからなかった場合、服用記録の修正に失敗する")
        fun takingRecordNotFound_modifyingTakingRecordFails() {
            //given:
            val badTakingRecordId = TakingRecordId("NonexistentId")

            //when:
            val target: () -> Unit = { takingRecordService.modifyTakingRecord(badTakingRecordId, command, userSession) }

            //then:
            val takingRecordNotFoundException = assertThrows<TakingRecordNotFoundException>(target)
            assertThat(takingRecordNotFoundException.takingRecordId).isEqualTo(badTakingRecordId)
        }

        @Test
        @DisplayName("薬が見つからなかった場合、服用記録の修正に失敗する")
        fun medicineNotFound_modifyingTakingRecordFails() {
            //given:
            val badMedicineId = MedicineId("NonexistentId")
            val command =
                    TestTakingRecordEditCommandFactory.createCompletedModificationCommand(takenMedicine = badMedicineId.value)

            //when:
            val target: () -> Unit = { takingRecordService.modifyTakingRecord(takingRecord.id, command, userSession) }

            //then:
            val medicineNotFoundException = assertThrows<MedicineNotFoundException>(target)
            assertThat(medicineNotFoundException.medicineId).isEqualTo(badMedicineId)
        }
    }

    @Nested
    inner class DeleteTakingRecordTest {
        private lateinit var takingRecord: TakingRecord

        @BeforeEach
        internal fun setUp() {
            takingRecord = testTakingRecordInserter.insert(userSession.accountId, requesterMedicine.id)
        }

        @Test
        @DisplayName("服用記録を削除する")
        fun deleteTakingRecord() {
            //when:
            takingRecordService.deleteTakingRecord(takingRecord.id, userSession)

            //then:
            val foundTakingRecord = takingRecordRepository.findById(takingRecord.id)
            assertThat(foundTakingRecord).isNull()
        }
    }
}
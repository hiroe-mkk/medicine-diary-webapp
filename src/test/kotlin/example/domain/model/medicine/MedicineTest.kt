package example.domain.model.medicine

import example.domain.model.account.*
import example.domain.model.sharedgroup.*
import example.domain.shared.type.*
import example.testhelper.factory.*
import org.assertj.core.api.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.*
import org.junit.jupiter.params.provider.*
import java.time.*

internal class MedicineTest {
    private val medicineName: MedicineName = MedicineName("ロキソニンSプレミアム")
    private val dosageAndAdministration: DosageAndAdministration = DosageAndAdministration(Dose(2.0),
                                                                                           "錠",
                                                                                           3,
                                                                                           listOf(Timing.AS_NEEDED))
    private val effects: Effects = Effects(listOf("頭痛", "解熱", "肩こり"))
    private val precautions: String = "服用間隔は4時間以上開けること。再度症状があらわれた場合には3回目を服用してもよい。"

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("所有する薬の基本情報を変更する")
    fun changeOwnedMedicineBasicInfo(isPublic: Boolean) {
        //given:
        val owner = MedicineOwner.create(AccountId("testAccountId"))
        val medicine = TestMedicineFactory.createMedicine(owner = owner)

        //when:
        medicine.changeBasicInfo(medicineName, dosageAndAdministration, effects, precautions, isPublic)

        //then:
        assertThat(medicine.isPublic).isEqualTo(isPublic)
    }

    @ParameterizedTest
    @CsvSource("true", "false")
    @DisplayName("共有グループの薬の基本情報を変更する")
    fun changeSharedGroupMedicineBasicInfo(isPublic: Boolean) {
        //given:
        val owner = MedicineOwner.create(SharedGroupId("testSharedGroupId"))
        val medicine = TestMedicineFactory.createMedicine(owner = owner)

        //when:
        medicine.changeBasicInfo(medicineName, dosageAndAdministration, effects, precautions, isPublic)

        //then:
        assertThat(medicine.isPublic).isTrue
    }
}
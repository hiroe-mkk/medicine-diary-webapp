package example.presentation.controller.api.medicationrecord

import example.application.service.account.*
import example.domain.model.medicine.*
import example.presentation.controller.api.profile.*
import example.presentation.shared.usersession.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import example.testhelper.springframework.security.*
import org.assertj.core.api.InstanceOfAssertFactories.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*
import org.springframework.security.test.web.servlet.request.*
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ControllerTest
internal class MedicationRecordApiControllerTest(@Autowired private val mockMvc: MockMvc,
                                                 @Autowired private val testMedicineInserter: TestMedicineInserter,
                                                 @Autowired private val testAccountInserter: TestAccountInserter,
                                                 @Autowired private val sharedGroupInserter: TestSharedGroupInserter,
                                                 @Autowired private val userSessionProvider: UserSessionProvider) {
    companion object {
        private const val PATH = "/api/medication-records"
    }

    @Test
    @WithMockAuthenticatedAccount
    @DisplayName("服用記録一覧を取得する")
    fun getMedicationRecords() {
        //given:
        val userSession = userSessionProvider.getUserSession()
        val member = testAccountInserter.insertAccountAndProfile().second
        sharedGroupInserter.insert(members = setOf(userSession.accountId, member.accountId))
        val medicine = testMedicineInserter.insert(MedicineOwner.create(userSession.accountId))

        //when:
        val actions = mockMvc.perform(get(PATH)
                                          .param("medicineid", medicine.id.value)
                                          .param("accountids", userSession.accountId.value)
                                          .param("accountids", member.accountId.value)
                                          .param("start", medicine.registeredAt.toLocalDate().toString()))

        //then:
        actions.andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/json"))
    }

    @Test
    @DisplayName("未認証ユーザによるリクエストの場合、ステータスコード401のレスポンスを返す")
    fun requestedByUnauthenticatedUser_returnsResponseWithStatus401() {
        //when:
        val actions = mockMvc.perform(get(PATH))

        //then:
        actions.andExpect(status().isUnauthorized)
    }
}
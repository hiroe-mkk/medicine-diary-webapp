package example.infrastructure.db.query.user

import example.application.query.user.*
import example.testhelper.inserter.*
import example.testhelper.springframework.autoconfigure.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.*

@MyBatisQueryServiceTest
internal class MyBatisJSONUserQueryServiceTest(@Autowired private val jsonUserQueryService: JSONUserQueryService,
                                               @Autowired private val testAccountInserter: TestAccountInserter,
                                               @Autowired private val testSharedGroupInserter: TestSharedGroupInserter) {
    @Test
    @DisplayName("共有グループメンバー一覧を取得する")
    fun getSharedGroupMembers() {
        //given:
        val (_, member1) = testAccountInserter.insertAccountAndProfile()
        val (_, member2) = testAccountInserter.insertAccountAndProfile()
        val sharedGroup = testSharedGroupInserter.insert(members = setOf(member1.accountId, member2.accountId))

        //when:
        val actual = jsonUserQueryService.getSharedGroupMembers(sharedGroup.id)

        //then:
        assertThat(actual.users)
            .extracting("accountId")
            .containsExactlyInAnyOrder(member1.accountId.value, member2.accountId.value)
    }
}

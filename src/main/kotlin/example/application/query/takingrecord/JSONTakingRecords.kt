package example.application.query.takingrecord

import org.springframework.data.domain.*

class JSONTakingRecords(val number: Int,
                        val totalPages: Int,
                        val takingRecords: List<JSONTakingRecord>) {
    companion object {
        fun from(page: Page<JSONTakingRecord>): JSONTakingRecords {
            return JSONTakingRecords(page.number, page.totalPages, page.content)
        }
    }
}
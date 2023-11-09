package example.infrastructure.query.takingrecord

import example.application.query.takingrecord.*
import org.apache.ibatis.annotations.*

@Mapper
interface TakingRecordDetailMapper {
    fun findOneByTakingRecordIdAndRecorder(takingRecordId: String): TakingRecordDetail?
}
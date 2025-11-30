package com.example.filepractice.poc.application

import com.example.filepractice.poc.domain.excel.AbstractExcelSheet
import com.example.filepractice.poc.domain.excel.ExcelData
import com.example.filepractice.poc.domain.excel.SheetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ExcelSheetIntegrator(
    private val sheets: List<AbstractExcelSheet<ExcelData>>,
) {
    /**
     * 동기 방식 엑셀 시트 작성
     * - 순차적으로 시트를 생성하고 데이터를 작성
     * - POI는 thread-safe하지 않으므로 순차 처리
     */
    fun composeSync(
        workbook: SXSSFWorkbook,
        excelDataMap: Map<SheetType, Sequence<ExcelData>>,
        sheetTypes: List<SheetType>,
    ) {
        sheets.forEach { sheetData ->
            if (sheetData.sheetType in sheetTypes) {
                val sheet = sheetData.createSheet(workbook)

                // Chunked 방식으로 메모리 사용 최적화
                val dataSequence = excelDataMap[sheetData.sheetType] ?: emptySequence()
                var currentRow = 1
                dataSequence.chunked(CHUNK_SIZE).forEach { chunk ->
                    currentRow = sheetData.writeData(sheet, chunk, currentRow)
                }
            }
        }
    }

    /**
     * 비동기 방식 엑셀 시트 작성 (개선 버전)
     * - runBlocking 대신 suspend 함수로 변경
     * - POI thread-safety 문제 해결: 시트 생성은 순차, 데이터 준비는 병렬
     * - 메모리 효율적인 처리
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun composeAsync(
        workbook: SXSSFWorkbook,
        excelDataMap: Map<SheetType, Sequence<ExcelData>>,
        sheetTypes: List<SheetType>,
    ) = withContext(Dispatchers.IO) {
        // 1단계: 시트별 데이터를 병렬로 List로 변환 (메모리에 로드)
        val sheetDataMap = ConcurrentHashMap<SheetType, List<List<ExcelData>>>()

        sheets
            .filter { it.sheetType in sheetTypes }
            .map { sheetData ->
                async(Dispatchers.Default) {
                    val dataSequence = excelDataMap[sheetData.sheetType] ?: emptySequence()
                    val chunkedData = dataSequence.chunked(CHUNK_SIZE).toList()
                    sheetDataMap[sheetData.sheetType] = chunkedData
                }
            }
            .awaitAll()

        // 2단계: POI 작업은 순차적으로 수행 (thread-safety 보장)
        // POI는 thread-safe하지 않으므로 워크북 접근은 단일 스레드에서
        withContext(Dispatchers.IO.limitedParallelism(1)) {
            sheets.forEach { sheetData ->
                if (sheetData.sheetType in sheetTypes) {
                    val sheet = sheetData.createSheet(workbook)
                    val chunkedData = sheetDataMap[sheetData.sheetType] ?: emptyList()

                    var currentRow = 1
                    chunkedData.forEach { chunk ->
                        currentRow = sheetData.writeData(sheet, chunk, currentRow)
                    }
                }
            }
        }
    }

    companion object {
        // 청크 크기를 100으로 증가 (메모리와 성능의 균형)
        const val CHUNK_SIZE = 100
    }
}

package com.example.filepractice.poc.application

import com.example.filepractice.poc.domain.excel.AbstractExcelSheet
import com.example.filepractice.poc.domain.excel.ExcelData
import com.example.filepractice.poc.domain.excel.SheetType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Component

@Component
class ExcelSheetIntegrator(
    private val sheets: List<AbstractExcelSheet<ExcelData>>,
) {
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

    fun composeAsyncByForkCoroutine(
        workbook: SXSSFWorkbook,
        excelDataMap: Map<SheetType, Sequence<ExcelData>>,
        sheetTypes: List<SheetType>,
    ) = runBlocking {
        val sheetMap = mutableMapOf<AbstractExcelSheet<ExcelData>, Sheet>()
        sheets.forEach { sheetData ->
            if (sheetData.sheetType in sheetTypes) {
                sheetMap[sheetData] = sheetData.createSheet(workbook)
            }
        }

        coroutineScope {
            sheetMap.map { (sheetData, sheet) ->
                async(Dispatchers.Default) {
                    val dataSequence = excelDataMap[sheetData.sheetType] ?: emptySequence()
                    writeSheetDataAsync(sheetData, sheet, dataSequence)
                }
            }.awaitAll()
        }
    }

    private suspend fun writeSheetDataAsync(
        sheetData: AbstractExcelSheet<ExcelData>,
        sheet: Sheet,
        dataSequence: Sequence<ExcelData>,
    ) {
        var currentRow = 1
        dataSequence.chunked(CHUNK_SIZE).forEach { chunk ->
            currentRow = sheetData.writeData(sheet, chunk, currentRow)
        }
    }

    companion object {
        const val CHUNK_SIZE = 10
    }
}

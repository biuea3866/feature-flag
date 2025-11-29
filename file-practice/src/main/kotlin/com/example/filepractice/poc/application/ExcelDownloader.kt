package com.example.filepractice.poc.application

import com.example.filepractice.poc.domain.excel.SheetType
import com.example.filepractice.poc.domain.excel.WorkbookType
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Component
import java.io.OutputStream

@Component
class ExcelDownloader(
    private val excelSheetIntegrator: ExcelSheetIntegrator,
    private val excelDataFetcher: ExcelDataFetcher,
) {
    /**
     * 비동기 방식 엑셀 다운로드 (기본)
     */
    fun write(
        outputStream: OutputStream,
        downloadableData: DownloadableData,
        workbookType: WorkbookType
    ) {
        val workbook = SXSSFWorkbook(STREAMING_SIZE)
            .apply { this.isCompressTempFiles = true }
        val excelDataMap = excelDataFetcher.fetchMapBy(downloadableData, SheetType.getSheetTypesBy(workbookType))

        try {
            outputStream.use {
                excelSheetIntegrator.composeAsyncByForkCoroutine(workbook, excelDataMap, SheetType.getSheetTypesBy(workbookType))
                workbook.write(it)
            }
        } finally {
            workbook.close()
            workbook.dispose()
        }
    }

    /**
     * 동기 방식 엑셀 다운로드 (성능 비교용)
     */
    fun writeSync(
        outputStream: OutputStream,
        downloadableData: DownloadableData,
        workbookType: WorkbookType
    ) {
        val workbook = SXSSFWorkbook(STREAMING_SIZE)
        val excelDataMap = excelDataFetcher.fetchMapBy(downloadableData, SheetType.getSheetTypesBy(workbookType))

        try {
            outputStream.use {
                excelSheetIntegrator.composeSync(workbook, excelDataMap, SheetType.getSheetTypesBy(workbookType))
                workbook.write(it)
            }
        } finally {
            workbook.close()
            workbook.dispose()
        }
    }

    companion object {
        private const val STREAMING_SIZE = 100
    }
}

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
     * 비동기 방식 엑셀 다운로드 (개선 버전)
     * - suspend 함수로 변경하여 스레드 블로킹 방지
     * - 압축 비활성화로 CPU 오버헤드 감소
     */
    suspend fun write(
        outputStream: OutputStream,
        downloadableData: DownloadableData,
        workbookType: WorkbookType
    ) {
        val workbook = SXSSFWorkbook(STREAMING_SIZE)
            // 압축 비활성화: CPU 사용량 감소, 디스크 I/O는 약간 증가하지만 전체적으로 더 빠름
            .apply { this.isCompressTempFiles = false }
        val excelDataMap = excelDataFetcher.fetchMapBy(downloadableData, SheetType.getSheetTypesBy(workbookType))

        try {
            outputStream.use {
                excelSheetIntegrator.composeAsync(workbook, excelDataMap, SheetType.getSheetTypesBy(workbookType))
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
            .apply { this.isCompressTempFiles = false }
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
        // 스트리밍 윈도우 크기: 메모리에 유지할 행 수
        // 100은 적절한 균형값 (너무 작으면 flush 오버헤드, 너무 크면 메모리 사용 증가)
        private const val STREAMING_SIZE = 100
    }
}

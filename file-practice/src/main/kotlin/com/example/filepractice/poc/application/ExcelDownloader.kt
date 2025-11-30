package com.example.filepractice.poc.application

import com.example.filepractice.poc.domain.excel.SheetType
import com.example.filepractice.poc.domain.excel.WorkbookType
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.OutputStream

@Component
class ExcelDownloader(
    private val excelSheetIntegrator: ExcelSheetIntegrator,
    private val excelDataFetcher: ExcelDataFetcher,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
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
        val startTime = System.currentTimeMillis()
        val runtime = Runtime.getRuntime()

        // 초기 메모리 측정
        runtime.gc()
        val startMemory = getUsedMemoryMB(runtime)
        logger.info("[비동기] 시작 - 메모리: ${startMemory}MB")

        val workbook = SXSSFWorkbook(STREAMING_SIZE)
            .apply { this.isCompressTempFiles = false }
        val excelDataMap = excelDataFetcher.fetchMapBy(downloadableData, SheetType.getSheetTypesBy(workbookType))

        val afterFetchMemory = getUsedMemoryMB(runtime)
        logger.info("[비동기] 데이터 준비 완료 - 메모리: ${afterFetchMemory}MB (+${afterFetchMemory - startMemory}MB)")

        try {
            outputStream.use {
                excelSheetIntegrator.composeAsync(workbook, excelDataMap, SheetType.getSheetTypesBy(workbookType))
                val afterComposeMemory = getUsedMemoryMB(runtime)
                logger.info("[비동기] 시트 작성 완료 - 메모리: ${afterComposeMemory}MB (+${afterComposeMemory - afterFetchMemory}MB)")

                workbook.write(it)
                val afterWriteMemory = getUsedMemoryMB(runtime)
                logger.info("[비동기] 파일 쓰기 완료 - 메모리: ${afterWriteMemory}MB (+${afterWriteMemory - afterComposeMemory}MB)")
            }
        } finally {
            workbook.close()
            workbook.dispose()

            runtime.gc()
            val endMemory = getUsedMemoryMB(runtime)
            val duration = System.currentTimeMillis() - startTime
            val totalMemoryUsed = endMemory - startMemory

            logger.info("[비동기] 완료 - 소요시간: ${duration}ms, 총 메모리 사용: ${totalMemoryUsed}MB, 최종 메모리: ${endMemory}MB")
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
        val startTime = System.currentTimeMillis()
        val runtime = Runtime.getRuntime()

        // 초기 메모리 측정
        runtime.gc()
        val startMemory = getUsedMemoryMB(runtime)
        logger.info("[동기] 시작 - 메모리: ${startMemory}MB")

        val workbook = SXSSFWorkbook(STREAMING_SIZE)
            .apply { this.isCompressTempFiles = false }
        val excelDataMap = excelDataFetcher.fetchMapBy(downloadableData, SheetType.getSheetTypesBy(workbookType))

        val afterFetchMemory = getUsedMemoryMB(runtime)
        logger.info("[동기] 데이터 준비 완료 - 메모리: ${afterFetchMemory}MB (+${afterFetchMemory - startMemory}MB)")

        try {
            outputStream.use {
                excelSheetIntegrator.composeSync(workbook, excelDataMap, SheetType.getSheetTypesBy(workbookType))
                val afterComposeMemory = getUsedMemoryMB(runtime)
                logger.info("[동기] 시트 작성 완료 - 메모리: ${afterComposeMemory}MB (+${afterComposeMemory - afterFetchMemory}MB)")

                workbook.write(it)
                val afterWriteMemory = getUsedMemoryMB(runtime)
                logger.info("[동기] 파일 쓰기 완료 - 메모리: ${afterWriteMemory}MB (+${afterWriteMemory - afterComposeMemory}MB)")
            }
        } finally {
            workbook.close()
            workbook.dispose()

            runtime.gc()
            val endMemory = getUsedMemoryMB(runtime)
            val duration = System.currentTimeMillis() - startTime
            val totalMemoryUsed = endMemory - startMemory

            logger.info("[동기] 완료 - 소요시간: ${duration}ms, 총 메모리 사용: ${totalMemoryUsed}MB, 최종 메모리: ${endMemory}MB")
        }
    }

    private fun getUsedMemoryMB(runtime: Runtime): Long {
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
    }

    companion object {
        // 스트리밍 윈도우 크기: 메모리에 유지할 행 수
        // 100은 적절한 균형값 (너무 작으면 flush 오버헤드, 너무 크면 메모리 사용 증가)
        private const val STREAMING_SIZE = 100
    }
}

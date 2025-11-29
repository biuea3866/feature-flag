package com.example.filepractice.poc.presentation

import com.example.filepractice.poc.application.DownloadableData
import com.example.filepractice.poc.application.ExcelDownloader
import com.example.filepractice.poc.domain.excel.WorkbookType
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/poc/excel")
class PocExcelDownloadController(
    private val excelDownloader: ExcelDownloader
) {

    /**
     * 비동기 방식 엑셀 다운로드
     */
    @GetMapping("/download/async")
    fun downloadExcelAsync(
        @RequestParam(defaultValue = "100") dataSize: Int,
        @RequestParam(defaultValue = "FULL") workbookType: WorkbookType
    ): ResponseEntity<StreamingResponseBody> {
        val downloadableData = createDownloadableData(dataSize)

        val streamingResponseBody = StreamingResponseBody { outputStream ->
            excelDownloader.write(outputStream, downloadableData, workbookType)
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition("poc_excel_async.xlsx"))
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(streamingResponseBody)
    }

    /**
     * 동기 방식 엑셀 다운로드
     */
    @GetMapping("/download/sync")
    fun downloadExcelSync(
        @RequestParam(defaultValue = "100") dataSize: Int,
        @RequestParam(defaultValue = "FULL") workbookType: WorkbookType
    ): ResponseEntity<StreamingResponseBody> {
        val downloadableData = createDownloadableData(dataSize)

        val streamingResponseBody = StreamingResponseBody { outputStream ->
            excelDownloader.writeSync(outputStream, downloadableData, workbookType)
        }

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition("poc_excel_sync.xlsx"))
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(streamingResponseBody)
    }

    private fun createDownloadableData(dataSize: Int): DownloadableData {
        val productIds = (1..dataSize).toList()
        val orderIds = (1..dataSize * 2).toList()
        val userIds = (1..dataSize).toList()
        val categoryIds = (1..dataSize / 5).toList()
        val reviewIds = (1..dataSize * 3).toList()
        val shipmentIds = (1..dataSize * 2).toList()

        return DownloadableData(
            productIds = productIds,
            orderIds = orderIds,
            userIds = userIds,
            categoryIds = categoryIds,
            reviewIds = reviewIds,
            shipmentIds = shipmentIds
        )
    }

    private fun getContentDisposition(filename: String): String {
        return ContentDisposition.attachment()
            .filename(filename, StandardCharsets.UTF_8)
            .build()
            .toString()
    }
}

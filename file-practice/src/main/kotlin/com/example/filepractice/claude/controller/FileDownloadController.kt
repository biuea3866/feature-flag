package com.example.filepractice.claude.controller

import com.example.filepractice.claude.dto.ColumnConfig
import com.example.filepractice.claude.dto.DownloadRequest
import com.example.filepractice.claude.service.AsyncFileDownloadService
import com.example.filepractice.claude.service.ExcelGenerationService
import com.example.filepractice.claude.service.OrderService
import com.example.filepractice.claude.service.PdfGenerationService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 파일 다운로드 컨트롤러
 *
 * 주문 내역을 엑셀 또는 PDF 파일로 다운로드하는 API를 제공합니다.
 * - 동기 다운로드: 즉시 파일을 생성하여 브라우저로 다운로드
 * - 비동기 다운로드: 백그라운드에서 파일을 생성하고 이메일로 발송
 */
@RestController
@RequestMapping("/api/claude/download")
class FileDownloadController(
    private val orderService: OrderService,
    private val excelGenerationService: ExcelGenerationService,
    private val pdfGenerationService: PdfGenerationService,
    private val asyncFileDownloadService: AsyncFileDownloadService
) {

    /**
     * 동기 방식 - 엑셀 다운로드
     *
     * 즉시 엑셀 파일을 생성하여 브라우저로 다운로드합니다.
     *
     * @param userId 사용자 ID
     * @param includePrice 가격 정보 포함 여부 (기본값: true)
     * @return 엑셀 파일
     */
    @GetMapping("/excel/sync")
    fun downloadExcelSync(
        @RequestParam userId: Long,
        @RequestParam(defaultValue = "true") includePrice: Boolean
    ): ResponseEntity<ByteArray> {
        // 주문 데이터 조회
        val orders = orderService.getOrdersByUserId(userId)

        // 컬럼 설정
        val columnConfig = if (includePrice) {
            ColumnConfig.default()
        } else {
            ColumnConfig.withoutPriceInfo()
        }

        // 엑셀 파일 생성
        val excelData = excelGenerationService.generateOrderExcel(orders, columnConfig)

        // HTTP 응답 헤더 설정
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        headers.setContentDispositionFormData("attachment", "order_history_${userId}.xlsx")

        return ResponseEntity(excelData, headers, HttpStatus.OK)
    }

    /**
     * 비동기 방식 - 엑셀 다운로드
     *
     * 백그라운드에서 엑셀 파일을 생성하고 이메일로 발송합니다.
     *
     * @param request 다운로드 요청 정보
     * @return 처리 상태 메시지
     */
    @PostMapping("/excel/async")
    fun downloadExcelAsync(@RequestBody request: DownloadRequest): ResponseEntity<Map<String, String>> {
        // 이메일 주소 필수 검증
        val email = request.email
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "이메일 주소가 필요합니다."))

        // 비동기 처리 시작
        asyncFileDownloadService.generateAndSendExcelAsync(
            userId = request.userId,
            email = email,
            columnConfig = request.columnConfig
        )

        val response = mapOf(
            "message" to "엑셀 파일 생성이 시작되었습니다. 완료되면 이메일로 발송됩니다.",
            "email" to email
        )

        return ResponseEntity.accepted().body(response)
    }

    /**
     * 동기 방식 - PDF 다운로드
     *
     * 즉시 PDF 파일을 생성하여 브라우저로 다운로드합니다.
     *
     * @param userId 사용자 ID
     * @return PDF 파일
     */
    @GetMapping("/pdf/sync")
    fun downloadPdfSync(@RequestParam userId: Long): ResponseEntity<ByteArray> {
        // 주문 데이터 조회
        val orders = orderService.getOrdersByUserId(userId)

        // PDF 파일 생성
        val pdfData = pdfGenerationService.generateOrderPdf(orders)

        // HTTP 응답 헤더 설정
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("attachment", "order_history_${userId}.pdf")

        return ResponseEntity(pdfData, headers, HttpStatus.OK)
    }

    /**
     * 비동기 방식 - PDF 다운로드
     *
     * 백그라운드에서 PDF 파일을 생성하고 이메일로 발송합니다.
     *
     * @param request 다운로드 요청 정보
     * @return 처리 상태 메시지
     */
    @PostMapping("/pdf/async")
    fun downloadPdfAsync(@RequestBody request: DownloadRequest): ResponseEntity<Map<String, String>> {
        // 이메일 주소 필수 검증
        val email = request.email
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "이메일 주소가 필요합니다."))

        // 비동기 처리 시작
        asyncFileDownloadService.generateAndSendPdfAsync(
            userId = request.userId,
            email = email
        )

        val response = mapOf(
            "message" to "PDF 파일 생성이 시작되었습니다. 완료되면 이메일로 발송됩니다.",
            "email" to email
        )

        return ResponseEntity.accepted().body(response)
    }

    /**
     * 헬스 체크 엔드포인트
     *
     * API 서버가 정상 동작하는지 확인합니다.
     */
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "OK"))
    }
}

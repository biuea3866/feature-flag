package com.example.filepractice.claude.service

import com.example.filepractice.claude.dto.ColumnConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 비동기 파일 다운로드 서비스
 *
 * 파일 생성 및 이메일 발송을 비동기로 처리합니다.
 * 실제 환경에서는 메시지 큐(RabbitMQ, Kafka 등)를 사용하는 것이 좋습니다.
 */
@Service
class AsyncFileDownloadService(
    private val orderService: OrderService,
    private val excelGenerationService: ExcelGenerationService,
    private val pdfGenerationService: PdfGenerationService,
    private val emailService: EmailService
) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * 엑셀 파일을 비동기로 생성하고 이메일로 발송
     *
     * @param userId 사용자 ID
     * @param email 수신자 이메일
     * @param columnConfig 컬럼 설정
     */
    fun generateAndSendExcelAsync(
        userId: Long,
        email: String,
        columnConfig: ColumnConfig
    ) {
        coroutineScope.launch {
            try {
                logger.info("비동기 엑셀 생성 시작: userId=$userId, email=$email")

                // 주문 데이터 조회
                val orders = orderService.getOrdersByUserId(userId)

                // 엑셀 파일 생성
                val excelData = excelGenerationService.generateOrderExcel(orders, columnConfig)

                // 이메일 발송
                emailService.sendOrderExcelEmail(email, excelData)

                logger.info("비동기 엑셀 생성 및 발송 완료: userId=$userId, email=$email")
            } catch (e: Exception) {
                logger.error("비동기 엑셀 생성 실패: userId=$userId, email=$email", e)
            }
        }
    }

    /**
     * PDF 파일을 비동기로 생성하고 이메일로 발송
     *
     * @param userId 사용자 ID
     * @param email 수신자 이메일
     */
    fun generateAndSendPdfAsync(
        userId: Long,
        email: String
    ) {
        coroutineScope.launch {
            try {
                logger.info("비동기 PDF 생성 시작: userId=$userId, email=$email")

                // 주문 데이터 조회
                val orders = orderService.getOrdersByUserId(userId)

                // PDF 파일 생성
                val pdfData = pdfGenerationService.generateOrderPdf(orders)

                // 이메일 발송
                emailService.sendOrderPdfEmail(email, pdfData)

                logger.info("비동기 PDF 생성 및 발송 완료: userId=$userId, email=$email")
            } catch (e: Exception) {
                logger.error("비동기 PDF 생성 실패: userId=$userId, email=$email", e)
            }
        }
    }
}

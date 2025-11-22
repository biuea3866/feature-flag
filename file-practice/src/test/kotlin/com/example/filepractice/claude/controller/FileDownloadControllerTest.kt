package com.example.filepractice.claude.controller

import com.example.filepractice.claude.domain.Order
import com.example.filepractice.claude.domain.Product
import com.example.filepractice.claude.dto.ColumnConfig
import com.example.filepractice.claude.dto.DownloadRequest
import com.example.filepractice.claude.service.AsyncFileDownloadService
import com.example.filepractice.claude.service.ExcelGenerationService
import com.example.filepractice.claude.service.OrderService
import com.example.filepractice.claude.service.PdfGenerationService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 파일 다운로드 컨트롤러 테스트
 */
@WebMvcTest(FileDownloadController::class)
class FileDownloadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var orderService: OrderService

    @MockBean
    private lateinit var excelGenerationService: ExcelGenerationService

    @MockBean
    private lateinit var pdfGenerationService: PdfGenerationService

    @MockBean
    private lateinit var asyncFileDownloadService: AsyncFileDownloadService

    /**
     * 더미 주문 데이터 생성
     */
    private fun createDummyOrders(): List<Order> {
        val product = Product(1L, "테스트 상품", BigDecimal("10000"), 1, "카테고리")
        val order = Order(
            id = 1L,
            orderNumber = "ORD-001",
            userId = 100L,
            products = listOf(product),
            coupon = null,
            totalAmount = BigDecimal("10000"),
            discountedAmount = BigDecimal("10000"),
            orderDate = LocalDateTime.now(),
            status = Order.OrderStatus.CONFIRMED
        )
        return listOf(order)
    }

    @Test
    fun `헬스 체크 테스트`() {
        // When & Then
        mockMvc.perform(get("/api/claude/download/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("OK"))
    }

    @Test
    fun `동기 엑셀 다운로드 테스트`() {
        // Given
        val userId = 100L
        val orders = createDummyOrders()
        val excelData = byteArrayOf(1, 2, 3, 4, 5)

        whenever(orderService.getOrdersByUserId(userId)).thenReturn(orders)
        whenever(excelGenerationService.generateOrderExcel(any(), any())).thenReturn(excelData)

        // When & Then
        mockMvc.perform(
            get("/api/claude/download/excel/sync")
                .param("userId", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .andExpect(header().exists("Content-Disposition"))
            .andExpect(content().bytes(excelData))
    }

    @Test
    fun `동기 엑셀 다운로드 테스트 - 가격 정보 제외`() {
        // Given
        val userId = 100L
        val orders = createDummyOrders()
        val excelData = byteArrayOf(1, 2, 3, 4, 5)

        whenever(orderService.getOrdersByUserId(userId)).thenReturn(orders)
        whenever(excelGenerationService.generateOrderExcel(any(), any())).thenReturn(excelData)

        // When & Then
        mockMvc.perform(
            get("/api/claude/download/excel/sync")
                .param("userId", userId.toString())
                .param("includePrice", "false")
        )
            .andExpect(status().isOk)
            .andExpect(content().bytes(excelData))
    }

    @Test
    fun `비동기 엑셀 다운로드 테스트`() {
        // Given
        val request = DownloadRequest(
            userId = 100L,
            email = "test@example.com",
            columnConfig = ColumnConfig.default()
        )

        // When & Then
        mockMvc.perform(
            post("/api/claude/download/excel/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isAccepted)
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.email").value("test@example.com"))
    }

    @Test
    fun `비동기 엑셀 다운로드 테스트 - 이메일 없음`() {
        // Given
        val request = DownloadRequest(
            userId = 100L,
            email = null,
            columnConfig = ColumnConfig.default()
        )

        // When & Then
        mockMvc.perform(
            post("/api/claude/download/excel/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("이메일 주소가 필요합니다."))
    }

    @Test
    fun `동기 PDF 다운로드 테스트`() {
        // Given
        val userId = 100L
        val orders = createDummyOrders()
        val pdfData = byteArrayOf(1, 2, 3, 4, 5)

        whenever(orderService.getOrdersByUserId(userId)).thenReturn(orders)
        whenever(pdfGenerationService.generateOrderPdf(orders)).thenReturn(pdfData)

        // When & Then
        mockMvc.perform(
            get("/api/claude/download/pdf/sync")
                .param("userId", userId.toString())
        )
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Type", "application/pdf"))
            .andExpect(header().exists("Content-Disposition"))
            .andExpect(content().bytes(pdfData))
    }

    @Test
    fun `비동기 PDF 다운로드 테스트`() {
        // Given
        val request = DownloadRequest(
            userId = 100L,
            email = "test@example.com"
        )

        // When & Then
        mockMvc.perform(
            post("/api/claude/download/pdf/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isAccepted)
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.email").value("test@example.com"))
    }

    @Test
    fun `비동기 PDF 다운로드 테스트 - 이메일 없음`() {
        // Given
        val request = DownloadRequest(
            userId = 100L,
            email = null
        )

        // When & Then
        mockMvc.perform(
            post("/api/claude/download/pdf/async")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("이메일 주소가 필요합니다."))
    }
}

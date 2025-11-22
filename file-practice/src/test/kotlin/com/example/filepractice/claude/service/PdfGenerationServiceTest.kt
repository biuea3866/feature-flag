package com.example.filepractice.claude.service

import com.example.filepractice.claude.domain.Coupon
import com.example.filepractice.claude.domain.Order
import com.example.filepractice.claude.domain.Product
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * PDF 생성 서비스 테스트
 */
class PdfGenerationServiceTest {

    private val pdfGenerationService = PdfGenerationService()

    /**
     * 더미 주문 데이터 생성
     */
    private fun createDummyOrders(): List<Order> {
        val product1 = Product(
            id = 1L,
            name = "테스트 상품 1",
            price = BigDecimal("50000"),
            quantity = 2,
            category = "전자기기"
        )

        val product2 = Product(
            id = 2L,
            name = "테스트 상품 2",
            price = BigDecimal("30000"),
            quantity = 1,
            category = "의류"
        )

        val coupon = Coupon(
            id = 1L,
            code = "TEST-COUPON",
            name = "10% 할인 쿠폰",
            discountRate = BigDecimal("0.1"),
            discountAmount = BigDecimal("13000")
        )

        val order = Order(
            id = 1L,
            orderNumber = "ORD-2025-000001",
            userId = 100L,
            products = listOf(product1, product2),
            coupon = coupon,
            totalAmount = BigDecimal("130000"),
            discountedAmount = BigDecimal("117000"),
            orderDate = LocalDateTime.of(2025, 1, 1, 10, 0),
            status = Order.OrderStatus.CONFIRMED
        )

        return listOf(order)
    }

    @Test
    fun `PDF 파일 생성 테스트`() {
        // Given: 더미 주문 데이터
        val orders = createDummyOrders()

        // When: PDF 파일 생성
        val pdfData = pdfGenerationService.generateOrderPdf(orders)

        // Then: PDF 파일이 정상적으로 생성되었는지 확인
        assertNotNull(pdfData)
        assertTrue(pdfData.isNotEmpty())

        // PDF 파일 형식 검증
        val pdfDocument = PdfDocument(PdfReader(ByteArrayInputStream(pdfData)))
        assertTrue(pdfDocument.numberOfPages > 0, "PDF 페이지가 생성되어야 합니다")
        pdfDocument.close()
    }

    @Test
    fun `PDF 파일 생성 테스트 - 빈 주문 목록`() {
        // Given: 빈 주문 목록
        val orders = emptyList<Order>()

        // When: PDF 파일 생성
        val pdfData = pdfGenerationService.generateOrderPdf(orders)

        // Then: PDF 파일이 생성되어야 함 (제목만 있음)
        assertNotNull(pdfData)
        assertTrue(pdfData.isNotEmpty())

        val pdfDocument = PdfDocument(PdfReader(ByteArrayInputStream(pdfData)))
        assertTrue(pdfDocument.numberOfPages > 0)
        pdfDocument.close()
    }

    @Test
    fun `PDF 파일 생성 테스트 - 쿠폰이 없는 주문`() {
        // Given: 쿠폰이 없는 주문 데이터
        val product = Product(1L, "상품", BigDecimal("10000"), 1, "카테고리")
        val order = Order(
            id = 1L,
            orderNumber = "ORD-001",
            userId = 100L,
            products = listOf(product),
            coupon = null,
            totalAmount = BigDecimal("10000"),
            discountedAmount = BigDecimal("10000"),
            orderDate = LocalDateTime.now(),
            status = Order.OrderStatus.PENDING
        )

        // When: PDF 파일 생성
        val pdfData = pdfGenerationService.generateOrderPdf(listOf(order))

        // Then: PDF 파일이 정상적으로 생성되어야 함
        assertNotNull(pdfData)
        assertTrue(pdfData.isNotEmpty())

        val pdfDocument = PdfDocument(PdfReader(ByteArrayInputStream(pdfData)))
        assertTrue(pdfDocument.numberOfPages > 0)
        pdfDocument.close()
    }

    @Test
    fun `PDF 파일 생성 테스트 - 대용량 데이터`() {
        // Given: 대용량 주문 데이터 (50개)
        val product = Product(1L, "상품", BigDecimal("10000"), 1, "카테고리")
        val orders = (1..50).map { index ->
            Order(
                id = index.toLong(),
                orderNumber = "ORD-$index",
                userId = 100L,
                products = listOf(product),
                coupon = null,
                totalAmount = BigDecimal("10000"),
                discountedAmount = BigDecimal("10000"),
                orderDate = LocalDateTime.now(),
                status = Order.OrderStatus.CONFIRMED
            )
        }

        // When: PDF 파일 생성
        val pdfData = pdfGenerationService.generateOrderPdf(orders)

        // Then: PDF 파일이 정상적으로 생성되어야 함
        assertNotNull(pdfData)
        assertTrue(pdfData.isNotEmpty())

        val pdfDocument = PdfDocument(PdfReader(ByteArrayInputStream(pdfData)))
        assertTrue(pdfDocument.numberOfPages > 0)
        pdfDocument.close()
    }
}

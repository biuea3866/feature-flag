package com.example.filepractice.claude.service

import com.example.filepractice.claude.domain.Coupon
import com.example.filepractice.claude.domain.Order
import com.example.filepractice.claude.domain.Product
import com.example.filepractice.claude.dto.ColumnConfig
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 엑셀 생성 서비스 테스트
 */
class ExcelGenerationServiceTest {

    private val excelGenerationService = ExcelGenerationService()

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
    fun `엑셀 파일 생성 테스트 - 모든 컬럼 포함`() {
        // Given: 더미 주문 데이터
        val orders = createDummyOrders()
        val columnConfig = ColumnConfig.default()

        // When: 엑셀 파일 생성
        val excelData = excelGenerationService.generateOrderExcel(orders, columnConfig)

        // Then: 엑셀 파일이 정상적으로 생성되었는지 확인
        assertNotNull(excelData)
        assertTrue(excelData.isNotEmpty())

        // 엑셀 파일 내용 검증
        val workbook = XSSFWorkbook(ByteArrayInputStream(excelData))
        assertEquals(2, workbook.numberOfSheets, "시트 개수는 2개여야 합니다")

        // 주문 내역 시트 검증
        val orderSheet = workbook.getSheetAt(0)
        assertEquals("주문 내역", orderSheet.sheetName)
        assertTrue(orderSheet.physicalNumberOfRows > 0, "주문 내역 시트에 데이터가 있어야 합니다")

        // 상품 시트 검증
        val productSheet = workbook.getSheetAt(1)
        assertEquals("상품", productSheet.sheetName)
        assertTrue(productSheet.physicalNumberOfRows > 0, "상품 시트에 데이터가 있어야 합니다")

        workbook.close()
    }

    @Test
    fun `엑셀 파일 생성 테스트 - 가격 정보 제외`() {
        // Given: 더미 주문 데이터와 가격 정보 제외 설정
        val orders = createDummyOrders()
        val columnConfig = ColumnConfig.withoutPriceInfo()

        // When: 엑셀 파일 생성
        val excelData = excelGenerationService.generateOrderExcel(orders, columnConfig)

        // Then: 엑셀 파일이 정상적으로 생성되었는지 확인
        assertNotNull(excelData)
        assertTrue(excelData.isNotEmpty())

        val workbook = XSSFWorkbook(ByteArrayInputStream(excelData))
        assertEquals(2, workbook.numberOfSheets)

        // 주문 내역 시트에서 가격 관련 컬럼이 제외되었는지 확인
        val orderSheet = workbook.getSheetAt(0)
        val headerRow = orderSheet.getRow(0)
        val headers = (0 until headerRow.lastCellNum).map { headerRow.getCell(it).stringCellValue }

        assertFalse(headers.contains("총 주문 금액"), "총 주문 금액 컬럼이 제외되어야 합니다")
        assertFalse(headers.contains("할인 후 금액"), "할인 후 금액 컬럼이 제외되어야 합니다")

        workbook.close()
    }

    @Test
    fun `엑셀 파일 생성 테스트 - 빈 주문 목록`() {
        // Given: 빈 주문 목록
        val orders = emptyList<Order>()
        val columnConfig = ColumnConfig.default()

        // When: 엑셀 파일 생성
        val excelData = excelGenerationService.generateOrderExcel(orders, columnConfig)

        // Then: 엑셀 파일이 생성되고 헤더만 있어야 함
        assertNotNull(excelData)
        assertTrue(excelData.isNotEmpty())

        val workbook = XSSFWorkbook(ByteArrayInputStream(excelData))
        val orderSheet = workbook.getSheetAt(0)

        // 헤더 행만 있어야 함 (데이터 행 없음)
        assertEquals(1, orderSheet.physicalNumberOfRows, "헤더 행만 있어야 합니다")

        workbook.close()
    }

    @Test
    fun `엑셀 파일 생성 테스트 - 대용량 데이터`() {
        // Given: 대용량 주문 데이터 (100개)
        val product = Product(1L, "상품", BigDecimal("10000"), 1, "카테고리")
        val orders = (1..100).map { index ->
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

        // When: 엑셀 파일 생성
        val excelData = excelGenerationService.generateOrderExcel(orders, ColumnConfig.default())

        // Then: 엑셀 파일이 정상적으로 생성되어야 함 (OOM 발생하지 않음)
        assertNotNull(excelData)
        assertTrue(excelData.isNotEmpty())

        val workbook = XSSFWorkbook(ByteArrayInputStream(excelData))
        val orderSheet = workbook.getSheetAt(0)

        // 헤더 + 데이터 행 개수 확인
        assertEquals(101, orderSheet.physicalNumberOfRows, "헤더 1개 + 데이터 100개 = 101개 행")

        workbook.close()
    }
}

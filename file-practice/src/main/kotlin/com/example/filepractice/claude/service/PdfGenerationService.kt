package com.example.filepractice.claude.service

import com.example.filepractice.claude.domain.Order
import com.itextpdf.io.font.PdfEncodings
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

/**
 * PDF 생성 서비스
 *
 * iText7 라이브러리를 사용하여 주문 내역을 PDF로 생성합니다.
 * 한글 폰트는 font-asian 패키지의 내장 폰트를 사용합니다.
 */
@Service
class PdfGenerationService {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * 주문 내역을 PDF 파일로 생성
     *
     * @param orders 주문 목록
     * @return PDF 파일 바이트 배열
     */
    fun generateOrderPdf(orders: List<Order>): ByteArray {
        val outputStream = ByteArrayOutputStream()
        val writer = PdfWriter(outputStream)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)

        try {
            // 한글 폰트 설정 (iText font-asian 패키지의 내장 한글 폰트 사용)
            val koreanFont = PdfFontFactory.createFont("HYSMyeongJo-Medium", "UniKS-UCS2-H")

            // 제목 추가
            addTitle(document, koreanFont)

            // 주문별로 상세 정보 표시
            orders.forEach { order ->
                addOrderSection(document, order, koreanFont)
                document.add(Paragraph("\n")) // 주문 간 여백
            }

        } finally {
            document.close()
        }

        return outputStream.toByteArray()
    }

    /**
     * 제목 추가
     */
    private fun addTitle(document: Document, font: PdfFont) {
        val title = Paragraph("주문 내역서")
            .setFont(font)
            .setFontSize(20f)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
        document.add(title)
        document.add(Paragraph("\n"))
    }

    /**
     * 주문 섹션 추가
     */
    private fun addOrderSection(document: Document, order: Order, font: PdfFont) {
        // 주문 정보 제목
        val orderTitle = Paragraph("주문 번호: ${order.orderNumber}")
            .setFont(font)
            .setFontSize(14f)
            .setBold()
        document.add(orderTitle)

        // 주문 기본 정보 테이블
        addOrderInfoTable(document, order, font)

        // 상품 목록 테이블
        addProductTable(document, order, font)

        // 쿠폰 정보 (있는 경우)
        order.coupon?.let { coupon ->
            addCouponInfo(document, coupon, font)
        }

        // 구분선
        document.add(Paragraph("─".repeat(80))
            .setFont(font)
            .setFontSize(10f))
    }

    /**
     * 주문 기본 정보 테이블 추가
     */
    private fun addOrderInfoTable(document: Document, order: Order, font: PdfFont) {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(30f, 70f)))
            .setWidth(UnitValue.createPercentValue(100f))

        // 헤더 스타일 적용 함수
        fun createHeaderCell(text: String): Cell {
            return Cell()
                .add(Paragraph(text).setFont(font).setFontSize(10f))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold()
        }

        fun createDataCell(text: String): Cell {
            return Cell()
                .add(Paragraph(text).setFont(font).setFontSize(10f))
        }

        table.addCell(createHeaderCell("주문 ID"))
        table.addCell(createDataCell(order.id.toString()))

        table.addCell(createHeaderCell("사용자 ID"))
        table.addCell(createDataCell(order.userId.toString()))

        table.addCell(createHeaderCell("주문 일시"))
        table.addCell(createDataCell(order.orderDate.format(dateFormatter)))

        table.addCell(createHeaderCell("주문 상태"))
        table.addCell(createDataCell(order.status.name))

        table.addCell(createHeaderCell("총 금액"))
        table.addCell(createDataCell("${order.totalAmount}원"))

        table.addCell(createHeaderCell("할인 후 금액"))
        table.addCell(createDataCell("${order.discountedAmount}원"))

        document.add(table)
        document.add(Paragraph("\n"))
    }

    /**
     * 상품 목록 테이블 추가
     */
    private fun addProductTable(document: Document, order: Order, font: PdfFont) {
        val sectionTitle = Paragraph("주문 상품 목록")
            .setFont(font)
            .setFontSize(12f)
            .setBold()
        document.add(sectionTitle)

        val table = Table(UnitValue.createPercentArray(floatArrayOf(10f, 30f, 15f, 15f, 15f, 15f)))
            .setWidth(UnitValue.createPercentValue(100f))

        // 헤더
        listOf("상품ID", "상품명", "가격", "수량", "카테고리", "합계").forEach { header ->
            table.addCell(
                Cell()
                    .add(Paragraph(header).setFont(font).setFontSize(9f))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // 데이터
        order.products.forEach { product ->
            val subtotal = product.price.multiply(product.quantity.toBigDecimal())

            table.addCell(Cell().add(Paragraph(product.id.toString()).setFont(font).setFontSize(9f)))
            table.addCell(Cell().add(Paragraph(product.name).setFont(font).setFontSize(9f)))
            table.addCell(Cell().add(Paragraph("${product.price}원").setFont(font).setFontSize(9f)))
            table.addCell(Cell().add(Paragraph(product.quantity.toString()).setFont(font).setFontSize(9f)))
            table.addCell(Cell().add(Paragraph(product.category).setFont(font).setFontSize(9f)))
            table.addCell(Cell().add(Paragraph("${subtotal}원").setFont(font).setFontSize(9f)))
        }

        document.add(table)
        document.add(Paragraph("\n"))
    }

    /**
     * 쿠폰 정보 추가
     */
    private fun addCouponInfo(
        document: Document,
        coupon: com.example.filepractice.claude.domain.Coupon,
        font: PdfFont
    ) {
        val couponInfo = Paragraph("적용된 쿠폰: ${coupon.name} (${coupon.code}) - 할인: ${coupon.discountAmount}원")
            .setFont(font)
            .setFontSize(10f)
            .setItalic()
        document.add(couponInfo)
        document.add(Paragraph("\n"))
    }
}

package com.example.filepractice.claude.service

import com.example.filepractice.claude.domain.Order
import com.example.filepractice.claude.dto.ColumnConfig
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.format.DateTimeFormatter

/**
 * 엑셀 생성 서비스
 *
 * SXSSFWorkbook을 사용하여 대용량 데이터 처리 시 OOM 문제를 방지합니다.
 * 메모리에 일정량의 행만 유지하고 나머지는 디스크에 임시 저장합니다.
 */
@Service
class ExcelGenerationService {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    /**
     * 주문 내역을 엑셀 파일로 생성
     *
     * @param orders 주문 목록
     * @param columnConfig 컬럼 설정
     * @return 엑셀 파일 바이트 배열
     */
    fun generateOrderExcel(orders: List<Order>, columnConfig: ColumnConfig): ByteArray {
        // SXSSFWorkbook: 메모리에 100개 행만 유지하고 나머지는 디스크에 임시 저장
        val workbook = SXSSFWorkbook(100)

        try {
            createOrderSheet(workbook, orders, columnConfig)
            createProductSheet(workbook, orders, columnConfig)

            val outputStream = ByteArrayOutputStream()
            workbook.write(outputStream)
            return outputStream.toByteArray()
        } finally {
            // 임시 파일 정리
            workbook.dispose()
            workbook.close()
        }
    }

    /**
     * 주문 내역 시트 생성
     */
    private fun createOrderSheet(
        workbook: SXSSFWorkbook,
        orders: List<Order>,
        columnConfig: ColumnConfig
    ) {
        val sheet = workbook.createSheet("주문 내역")
        val headerStyle = createHeaderStyle(workbook)

        // 헤더 행 생성
        val headerRow = sheet.createRow(0)
        val orderColumns = columnConfig.orderColumns.toList()

        orderColumns.forEachIndexed { index, column ->
            val cell = headerRow.createCell(index)
            cell.setCellValue(column.displayName)
            cell.cellStyle = headerStyle
        }

        // 데이터 행 생성
        orders.forEachIndexed { orderIndex, order ->
            val row = sheet.createRow(orderIndex + 1)

            orderColumns.forEachIndexed { columnIndex, column ->
                val cell = row.createCell(columnIndex)
                val value = getOrderColumnValue(order, column)
                cell.setCellValue(value)
            }
        }

        // 컬럼 너비 설정 (SXSSFWorkbook에서는 autoSizeColumn 사용 불가)
        orderColumns.indices.forEach { i ->
            sheet.setColumnWidth(i, 4000) // 약 20자 너비
        }
    }

    /**
     * 상품 시트 생성
     *
     * 모든 주문의 상품을 하나의 시트에 펼쳐서 표시합니다.
     */
    private fun createProductSheet(
        workbook: SXSSFWorkbook,
        orders: List<Order>,
        columnConfig: ColumnConfig
    ) {
        val sheet = workbook.createSheet("상품")
        val headerStyle = createHeaderStyle(workbook)

        // 헤더 행 생성 (주문 번호 + 상품 컬럼)
        val headerRow = sheet.createRow(0)
        var columnIndex = 0

        // 주문 번호 컬럼 추가
        headerRow.createCell(columnIndex++).apply {
            setCellValue("주문 번호")
            cellStyle = headerStyle
        }

        val productColumns = columnConfig.productColumns.toList()
        productColumns.forEach { column ->
            val cell = headerRow.createCell(columnIndex++)
            cell.setCellValue(column.displayName)
            cell.cellStyle = headerStyle
        }

        // 데이터 행 생성
        var rowIndex = 1
        orders.forEach { order ->
            order.products.forEach { product ->
                val row = sheet.createRow(rowIndex++)
                var cellIndex = 0

                // 주문 번호
                row.createCell(cellIndex++).setCellValue(order.orderNumber)

                // 상품 정보
                productColumns.forEach { column ->
                    val cell = row.createCell(cellIndex++)
                    val value = getProductColumnValue(product, column)
                    cell.setCellValue(value)
                }
            }
        }

        // 컬럼 너비 설정 (SXSSFWorkbook에서는 autoSizeColumn 사용 불가)
        for (i in 0 until productColumns.size + 1) {
            sheet.setColumnWidth(i, 4000) // 약 20자 너비
        }
    }

    /**
     * 헤더 스타일 생성
     */
    private fun createHeaderStyle(workbook: SXSSFWorkbook): CellStyle {
        return workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            setFont(workbook.createFont().apply {
                bold = true
            })
        }
    }

    /**
     * 주문 컬럼 값 조회
     */
    private fun getOrderColumnValue(order: Order, column: ColumnConfig.OrderColumn): String {
        return when (column) {
            ColumnConfig.OrderColumn.ID -> order.id.toString()
            ColumnConfig.OrderColumn.ORDER_NUMBER -> order.orderNumber
            ColumnConfig.OrderColumn.USER_ID -> order.userId.toString()
            ColumnConfig.OrderColumn.TOTAL_AMOUNT -> order.totalAmount.toString()
            ColumnConfig.OrderColumn.DISCOUNTED_AMOUNT -> order.discountedAmount.toString()
            ColumnConfig.OrderColumn.ORDER_DATE -> order.orderDate.format(dateFormatter)
            ColumnConfig.OrderColumn.STATUS -> order.status.name
        }
    }

    /**
     * 상품 컬럼 값 조회
     */
    private fun getProductColumnValue(
        product: com.example.filepractice.claude.domain.Product,
        column: ColumnConfig.ProductColumn
    ): String {
        return when (column) {
            ColumnConfig.ProductColumn.ID -> product.id.toString()
            ColumnConfig.ProductColumn.NAME -> product.name
            ColumnConfig.ProductColumn.PRICE -> product.price.toString()
            ColumnConfig.ProductColumn.QUANTITY -> product.quantity.toString()
            ColumnConfig.ProductColumn.CATEGORY -> product.category
        }
    }
}

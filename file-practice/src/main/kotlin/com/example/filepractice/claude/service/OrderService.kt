package com.example.filepractice.claude.service

import com.example.filepractice.claude.domain.Coupon
import com.example.filepractice.claude.domain.Order
import com.example.filepractice.claude.domain.Product
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * 주문 서비스
 *
 * 주문 데이터를 조회하고 관리하는 서비스입니다.
 * 현재는 더미 데이터를 생성하지만, 실제 환경에서는 데이터베이스에서 조회합니다.
 */
@Service
class OrderService {

    /**
     * 사용자의 주문 내역 조회
     *
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    fun getOrdersByUserId(userId: Long): List<Order> {
        // 실제 환경에서는 데이터베이스에서 조회
        // 현재는 더미 데이터를 생성하여 반환
        return generateDummyOrders(userId)
    }

    /**
     * 더미 주문 데이터 생성
     *
     * 테스트 및 예제를 위한 더미 데이터를 생성합니다.
     */
    private fun generateDummyOrders(userId: Long): List<Order> {
        val orders = mutableListOf<Order>()

        repeat(5) { index ->
            val orderId = index + 1L
            val orderNumber = "ORD-2025-${String.format("%06d", orderId)}"

            // 상품 생성 (2-3개)
            val productCount = Random.nextInt(2, 4)
            val products = (1..productCount).map { productIndex ->
                val productId = orderId * 100 + productIndex
                val price = BigDecimal(Random.nextInt(10_000, 100_000))

                Product(
                    id = productId,
                    name = "상품 ${productId}",
                    price = price,
                    quantity = Random.nextInt(1, 5),
                    category = listOf("전자기기", "의류", "식품", "생활용품", "도서").random()
                )
            }

            // 총 금액 계산
            val totalAmount = products.sumOf { it.price.multiply(it.quantity.toBigDecimal()) }

            // 쿠폰 생성 (50% 확률)
            val coupon = if (Random.nextBoolean()) {
                val discountRate = BigDecimal("0.${Random.nextInt(5, 20)}")
                val discountAmount = totalAmount.multiply(discountRate)
                Coupon(
                    id = orderId,
                    code = "COUPON-${orderId}",
                    name = "${(discountRate.multiply(BigDecimal(100))).toInt()}% 할인 쿠폰",
                    discountRate = discountRate,
                    discountAmount = discountAmount
                )
            } else null

            val discountedAmount = coupon?.let { totalAmount.subtract(it.discountAmount) } ?: totalAmount

            val order = Order(
                id = orderId,
                orderNumber = orderNumber,
                userId = userId,
                products = products,
                coupon = coupon,
                totalAmount = totalAmount,
                discountedAmount = discountedAmount,
                orderDate = LocalDateTime.now().minusDays(Random.nextLong(1, 30)),
                status = Order.OrderStatus.entries.random()
            )

            orders.add(order)
        }

        return orders
    }
}

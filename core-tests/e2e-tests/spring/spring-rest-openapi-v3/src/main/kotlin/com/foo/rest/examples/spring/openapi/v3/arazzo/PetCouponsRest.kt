package com.foo.rest.examples.spring.openapi.v3.arazzo

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

/**
 * Arazzo E2E tests.
 */
@RestController
class PetCouponsRest {

    private val orderIds = AtomicLong(1)

    @GetMapping(path = ["/pet/findByTags"])
    fun findPetsByTags(
        @RequestParam(required = false) tags: List<String>?,
    ): ResponseEntity<List<PetDto>> {
        return ResponseEntity.ok(
            listOf(
                PetDto(
                    id = 1,
                    name = "doggie",
                    photoUrls = listOf("http://example.com/photo.jpg"),
                    100,
                    status = "available",
                )
            )
        )
    }

    @GetMapping(path = ["/pet/findByStatus"])
    fun findPetsByStatus(
        @RequestParam(required = false) status: String?,
        @RequestParam page: Int,
        @RequestParam(required = false, defaultValue = "10") pageSize: Int,
    ): ResponseEntity<List<PetDto>> {
        return ResponseEntity.ok(
            listOf(
                PetDto(
                    id = 1,
                    name = "doggie",
                    photoUrls = listOf("http://example.com/photo.jpg"),
                    100,
                    status = "available",
                )
            )
        )
    }

    @GetMapping(path = ["/pet/{petId}/coupons"])
    fun getPetCoupons(
        @PathVariable petId: Long,
    ): ResponseEntity<CouponDto> {
        return ResponseEntity.ok(
            CouponDto(
                id = 10,
                description = "Summer Sale - 10% off!",
                couponCode = "SUMMERSALE",
            )
        )
    }

    @PostMapping(
        path = ["/store/order"],
        consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE]
    )
    fun placeOrder(
        @RequestBody order: OrderDto,
    ): ResponseEntity<OrderDto> {
        return ResponseEntity.ok(
            OrderDto(
                id = orderIds.getAndIncrement(),
                petId = order.petId,
                quantity = order.quantity ?: 1,
                status = order.status ?: "placed",
                complete = order.complete ?: false,
                couponCode = order.couponCode,
            )
        )
    }
}

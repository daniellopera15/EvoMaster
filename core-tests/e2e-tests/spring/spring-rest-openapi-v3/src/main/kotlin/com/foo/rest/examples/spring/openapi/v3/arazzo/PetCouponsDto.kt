package com.foo.rest.examples.spring.openapi.v3.arazzo

data class PetDto(
    val id: Long? = null,
    val name: String? = null,
    val photoUrls: List<String>? = null,
    val price: Number?,
    val status: String? = null,
)

data class CouponDto(
    val id: Long? = null,
    val description: String? = null,
    val couponCode: String? = null,
)

data class OrderDto(
    val id: Long? = null,
    val petId: Long? = null,
    val quantity: Int? = null,
    val status: String? = null,
    val complete: Boolean? = null,
    val couponCode: String? = null,
)

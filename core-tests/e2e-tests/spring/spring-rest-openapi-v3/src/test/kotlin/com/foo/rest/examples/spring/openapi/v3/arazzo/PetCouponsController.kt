package com.foo.rest.examples.spring.openapi.v3.arazzo

import com.foo.rest.examples.spring.openapi.v3.SpringController
import org.evomaster.client.java.controller.problem.ProblemInfo
import org.evomaster.client.java.controller.problem.RestProblem

class PetCouponsController : SpringController(ArazzoApplication::class.java) {

    override fun getProblemInfo(): ProblemInfo {
        return RestProblem(
            "http://localhost:$sutPort/openapi-pet-coupons.yaml",
            null,
        )
    }
}

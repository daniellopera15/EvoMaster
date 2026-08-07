package org.evomaster.e2etests.spring.openapi.v3.arazzo

import com.foo.rest.examples.spring.openapi.v3.arazzo.PetCouponsController
import org.evomaster.e2etests.spring.openapi.v3.SpringTestBase
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.File

class ArazzoPetCouponsEMTest : SpringTestBase() {

    companion object {
        private const val ARAZZO_RELATIVE_PATH = "src/test/resources/arazzo/pet-coupons-arazzo.yaml"

        @BeforeAll
        @JvmStatic
        fun init() {
            initClass(PetCouponsController())
        }

        private fun arazzoPath(): String {
            val file = File(ARAZZO_RELATIVE_PATH)
            check(file.exists()) { "Missing arazzo file: ${file.absolutePath}" }
            return file.absolutePath
        }
    }

    @Test
    fun testRunEM() {
        runTestHandlingFlakyAndCompilation(
            "ArazzoPetCouponsEM",
            "org.foo.ArazzoPetCouponsEM",
            20,
        ) { args: MutableList<String> ->

            setOption(args, "arazzoStrategy", "ENABLED")
            setOption(args, "arazzoLocation", arazzoPath())

            val solution = initAndRun(args)

            assertTrue(solution.individuals.size >= 1)
        }
    }
}

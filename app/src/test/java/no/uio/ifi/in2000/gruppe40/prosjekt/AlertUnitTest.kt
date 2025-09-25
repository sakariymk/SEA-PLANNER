package no.uio.ifi.in2000.gruppe40.prosjekt

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.MetalertRepository
import no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert.MetalertRetrofit
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class AlertUnitTest {

    @Test
    fun alertTest() {
        runBlocking {
            val metalertRepository = MetalertRepository(MetalertRetrofit.api)
            assertNotNull(metalertRepository)
        }
    }
}

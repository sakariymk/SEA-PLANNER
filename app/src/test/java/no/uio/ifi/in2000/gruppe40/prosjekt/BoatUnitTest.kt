package no.uio.ifi.in2000.gruppe40.prosjekt

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.gruppe40.prosjekt.data.boat.BoatRepository
import no.uio.ifi.in2000.gruppe40.prosjekt.data.boat.getBoatAPI
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class BoatUnitTest {


    @Test
    fun boatRepositoryTest() {
        runBlocking {
            val repository = BoatRepository()
            repository.createRepository()

            assertNotNull(repository)
        }
    }

    @Test
    fun boatApiTest() {
        runBlocking {
            assertNotNull(getBoatAPI())
        }
    }

    @Test
    fun boatTestData() {
        runBlocking {
            val boatRepository = BoatRepository()
            boatRepository.createRepository()

            assertNotNull(boatRepository.getBoatList())
            assertNotNull(boatRepository.getFishingBoats())
            assertNotNull(boatRepository.getCommercialBoats())
            assertNotNull(boatRepository.getDivingBoats())
            assertNotNull(boatRepository.getTowingBoats())
            assertNotNull(boatRepository.getLawEnforcmentBoats())
            assertNotNull(boatRepository.getSailingBoats())
            assertNotNull(boatRepository.getMedicalBoats())
        }
    }
}

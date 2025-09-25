package no.uio.ifi.in2000.gruppe40.prosjekt.data.boat

class BoatRepository {
    private var boatRepository: List<Boat> = emptyList()

    suspend fun createRepository() {
        boatRepository = getBoatAPI()
    }

    fun getBoatList(): List<Boat> {
        return boatRepository
    }

    // the following functions returns types of boats:

    fun getFishingBoats(): List <Boat> {
        val fishingboats = boatRepository.filter { it.shipType?.toInt() == 30 }
        return fishingboats
    }

    fun getTowingBoats() : List <Boat> {
        val towingboats = boatRepository.filter { it.shipType?.toInt() == 31 }
        return towingboats
    }

    fun getSailingBoats(): List <Boat> {
        val sailingboats = boatRepository.filter { it.shipType?.toInt() == 36 }
        return sailingboats
    }

    fun getCommercialBoats(): List <Boat> {
        val commercialboats = boatRepository.filter { boat ->
            val type = boat.shipType?.toInt()
            type == 36 || (type in 60..66) || (type in 70..99)
        }
        return commercialboats
    }

    fun getLawEnforcmentBoats(): List <Boat> {
        val enforcmentboats = boatRepository.filter { it.shipType?.toInt() == 35 || it.shipType?.toInt() == 55 }
        return enforcmentboats
    }

    fun getDivingBoats(): List <Boat> {
        val divingboats = boatRepository.filter { it.shipType?.toInt() == 3 || it.shipType?.toInt() == 34 }
        return divingboats
    }

    fun getMedicalBoats(): List <Boat> {
        val medicalboats = boatRepository.filter{it.shipType?.toInt() == 58}
        return medicalboats
    }

}

package no.uio.ifi.in2000.gruppe40.prosjekt.data.metalert

//Repository that fetches warnings via MetalertAPI
class MetalertRepository(private val api: MetalertAPI) {
    //fetches alert for a geographic domain and returns a MetalertResponse
    suspend fun fetchWarning(domain: String): Result<MetalertResponse> {
        return try {
            val response = api.getMetalerts(domain = domain)
            Result.success(response) //Returns data if successful
        } catch (e: Exception) {
            Result.failure(e) //Returns failure if exception occurs
        }
    }
}
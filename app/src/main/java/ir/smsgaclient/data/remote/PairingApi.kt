// app/src/main/java/ir/smsgaclient/data/remote/PairingApi.kt
package ir.smsgaclient.data.remote

import ir.smsgaclient.data.remote.dto.PairPollRequest
import ir.smsgaclient.data.remote.dto.PairPollResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PairingApi {

    @POST("v1/pair/poll")
    suspend fun pollPairingStatus(
        @Body request: PairPollRequest
    ): Response<PairPollResponse>
}

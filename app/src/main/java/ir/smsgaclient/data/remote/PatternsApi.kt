// app/src/main/java/ir/smsgaclient/data/remote/PatternsApi.kt
package ir.smsgaclient.data.remote

import ir.smsgaclient.domain.model.PatternSet
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface PatternsApi {

    @GET("v1/patterns")
    suspend fun getPatterns(
        @Header("If-None-Match") currentVersion: String? = null
    ): Response<PatternSet>
}

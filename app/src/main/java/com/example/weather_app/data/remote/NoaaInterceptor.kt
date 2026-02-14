package com.example.weather_app.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class NoaaInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(
                "User-Agent",
                "438Weather438"
            )
            .header(
                "Accept",
                "application/geo+json"
            )
            .build()

        return chain.proceed(request)
    }
}
package com.example.fixu.retrofit

import com.example.fixu.database.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        val protectedEndpoints = listOf(
            "/predict/professional/result",
            "/predict/student/result",
            "/history",
            "/notes",
            "/notes/add",
            "/notes/update/{id}",
            "notes/{id}"
        )

        val needsAuth = protectedEndpoints.any { endpoint -> url.contains(endpoint) }

        val requestBuilder = request.newBuilder()
        if (needsAuth) {
            val token = sessionManager.getToken()
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}

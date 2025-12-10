package com.example.paymyfinesstep.api



import com.example.paymyfinesstep.cart.CartItemRequest
import com.example.paymyfinesstep.cart.CartItemResponse
import retrofit2.Response
import retrofit2.http.*

interface CartApi {

    @POST("cart/add")
    suspend fun addToCart(@Body item: CartItemRequest): Response<ApiMessageResponse>

    @GET("cart")
    suspend fun getCart(): Response<List<CartItemResponse>>

    @DELETE("cart/remove/{noticeNumber}")
    suspend fun removeFromCart(@Path("noticeNumber") notice: String): Response<ApiMessageResponse>

    @DELETE("cart/clear")
    suspend fun clearCart(): Response<ApiMessageResponse>
}


data class ApiMessageResponse(val message: String)

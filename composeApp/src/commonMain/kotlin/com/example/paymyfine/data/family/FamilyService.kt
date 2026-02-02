package com.example.paymyfine.data.family

import com.example.paymyfine.data.family.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class FamilyService(
    private val client: HttpClient,
    private val baseUrl: String
) {
    suspend fun listMembers() =
        client.get("$baseUrl/family/list")

    suspend fun addMember(req: AddFamilyMemberRequest) =
        client.post("$baseUrl/family/add") {
            contentType(ContentType.Application.Json)
            setBody(req)
        }

    suspend fun deleteMember(linkId: String) =
        client.delete("$baseUrl/family/$linkId")
}

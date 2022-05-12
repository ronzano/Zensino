package app.ronzano.zensino.webservices

import app.ronzano.zensino.webservices.models.LoginRequest

class ZensiRepository(baseUrl: String) : BaseRepository() {

    private val client = ZensiServiceHandler.zensiService(baseUrl)

    suspend fun login(
        username: String,
        password: String,
    ) = apiCall { client.login(LoginRequest(username, password)) }

    suspend fun status(
        token: String
    ) = apiCall { client.status("Token $token") }
}
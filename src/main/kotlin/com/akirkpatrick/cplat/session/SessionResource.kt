package com.akirkpatrick.cplat.session

import com.akirkpatrick.cplat.account.Account
import com.akirkpatrick.cplat.account.AccountRepository
import com.akirkpatrick.cplat.config.SequenceProvider
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import io.undertow.util.FileUtils
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.NewCookie
import javax.ws.rs.core.Response
import kotlin.collections.listOf

@Component
@Path("/session")
public open class SessionResource @Autowired constructor(
        val sessions: SessionRepository,
        val accounts: AccountRepository,
        val seq: SequenceProvider) {

    val log = LoggerFactory.getLogger(javaClass<SessionResource>())

    @POST
    @Consumes("application/json")
    fun login(loginInfo: LoginInfo): Response {
        val userInfo = getUserInfo(loginInfo)

        val account = getOrCreateAccount(userInfo)

        // TODO: H: modify expires
        val session = createSession(account, userInfo.expires)

        val maxAge = 30 * 60; // default 30 mins
        val cookie = NewCookie("sid", session.id.toString(), "/", null, null, maxAge.toInt(), false)

        return Response.ok().entity(account)
                .cookie(cookie)
                .build()
    }

    @GET
    @Path("{sid}")
    fun login(@PathParam("sid") sid: String): Account {
        val session = sessions.findOne(UUID.fromString(sid)) ?: throw IllegalArgumentException("Session not found: $sid")
        // TODO: H: check session expiry
        return accounts.findById(session.userId) ?: throw IllegalArgumentException("Account not found: ${session.userId}")
    }

    private fun createSession(account: Account, expires: Date): Session {
        val session = Session(UUID.randomUUID(), account.id, expires)
        sessions.save(session)
        return session
    }

    private fun getOrCreateAccount(userInfo: UserInfo): Account {
        var account = accounts.findByEmailAddress(userInfo.email)
        if ( account == null ) {
            val id = seq.nextLong("account")
            account = Account(id, "xxx")
            account.email.add(Account.Email(userInfo.email, true))

            accounts.save(account)
        }
        return account
    }

    private fun getUserInfo(loginInfo: LoginInfo): UserInfo {
        val entity = UrlEncodedFormEntity(listOf(
                Param("assertion", loginInfo.assertion),
                Param("audience", "http://localhost:8080")
        ))

        log.debug("Building post with assertion: {}", loginInfo.assertion)

        val request = HttpPost("https://verifier.login.persona.org/verify")
        request.setEntity(entity)

        val client = HttpClientBuilder.create().build()
        val response = client.execute(request)
        val status = response.getStatusLine().getStatusCode()
        if ( status != 200 ) {
            throw IllegalArgumentException("Failed to validate assertion")
        }

        val content = FileUtils.readFile(response.getEntity().getContent())
        log.trace("Response from Persona: {}", content)

        val info = ResponseInfo.fromString(content)
        if ( info.status != "okay") {
            throw IllegalArgumentException("Failed to login: ${info.reason}")
        }

        log.debug("Successful login with Persona!")
        val userInfo = UserInfo.fromString(content);
        return userInfo
    }

    data class Param(val _name: String, val _value: String) : NameValuePair {
        override fun getName(): String = _name
        override fun getValue(): String = _value
    }

    data class LoginInfo @JsonCreator constructor(
            @JsonProperty("assertion") val assertion: String
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ResponseInfo @JsonCreator constructor(
            @JsonProperty("status") val status: String,
            @JsonProperty("reason") val reason: String?
    ) {
        companion object {
            fun fromString(text: String): ResponseInfo =
                    ObjectMapper().readValue(text, ResponseInfo::class.java)
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class UserInfo @JsonCreator constructor(
            @JsonProperty("email") val email: String,
            @JsonProperty("expires") val expires: Date
    ) {
        companion object {
            fun fromString(text: String): UserInfo =
                    ObjectMapper().readValue(text, UserInfo::class.java)
        }
    }
}


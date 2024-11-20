package com.frozenkro.dirtie_client.data.api

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieStore(context: Context) : CookieStore {
    private val prefs: SharedPreferences = context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE)
    private val cookieMap = ConcurrentHashMap<URI, MutableList<HttpCookie>>()

    init {
        // Load any stored cookies during initialization
        loadFromPrefs()
    }

    @Synchronized
    private fun loadFromPrefs() {
        // Clear existing cookies
        cookieMap.clear()

        // Load all cookies from preferences
        prefs.all.forEach { (key, value) ->
            if (value is String) {
                try {
                    // Each stored cookie is in format: "domain|path|name|value|maxAge|secure"
                    val cookieParts = value.split("|")
                    if (cookieParts.size >= 4) {
                        val domain = cookieParts[0]
                        val path = cookieParts[1]
                        val name = cookieParts[2]
                        val cookieValue = cookieParts[3]

                        val cookie = HttpCookie(name, cookieValue).apply {
                            this.domain = domain
                            this.path = path
                            if (cookieParts.size > 4) {
                                maxAge = cookieParts[4].toLongOrNull() ?: -1L
                            }
                            if (cookieParts.size > 5) {
                                secure = cookieParts[5].toBoolean()
                            }
                        }

                        val uri = getURIFromCookie(cookie)
                        addCookieToMap(uri, cookie)
                    }
                } catch (e: Exception) {
                    // If there's an error parsing a cookie, remove it
                    prefs.edit().remove(key).apply()
                }
            }
        }
    }

    override fun add(uri: URI?, cookie: HttpCookie?) {
        if (uri == null || cookie == null) return

        // Add to in-memory map
        addCookieToMap(uri, cookie)

        // Save to preferences
        saveCookieToPrefs(cookie)
    }

    override fun get(uri: URI?): List<HttpCookie> {
        if (uri == null) return emptyList()

        val cookies = mutableListOf<HttpCookie>()

        // Get cookies for exact URI
        cookieMap[uri]?.let { cookies.addAll(it) }

        // Get cookies for parent domains
        val host = uri.host ?: return cookies
        cookies.addAll(getCookiesForHost(host))

        return cookies.filter { !it.hasExpired() }
    }

    override fun getCookies(): List<HttpCookie> {
        return cookieMap.values.flatten().filter { !it.hasExpired() }
    }

    override fun getURIs(): List<URI> {
        return cookieMap.keys.toList()
    }

    override fun remove(uri: URI?, cookie: HttpCookie?): Boolean {
        if (uri == null || cookie == null) return false

        val removed = cookieMap[uri]?.remove(cookie) ?: false
        if (removed) {
            // Remove from preferences
            val key = getCookieKey(cookie)
            prefs.edit().remove(key).apply()
        }
        return removed
    }

    override fun removeAll(): Boolean {
        cookieMap.clear()
        prefs.edit().clear().apply()
        return true
    }

    private fun addCookieToMap(uri: URI, cookie: HttpCookie) {
        val cookies = cookieMap.getOrPut(uri) { mutableListOf() }
        // Remove any existing cookie with same name
        cookies.removeAll { it.name == cookie.name }
        cookies.add(cookie)
    }

    private fun saveCookieToPrefs(cookie: HttpCookie) {
        val key = getCookieKey(cookie)
        val value = "${cookie.domain}|${cookie.path}|${cookie.name}|${cookie.value}|${cookie.maxAge}|${cookie.secure}"
        prefs.edit().putString(key, value).apply()
    }

    private fun getCookieKey(cookie: HttpCookie): String {
        return "${cookie.domain}_${cookie.path}_${cookie.name}"
    }

    private fun getURIFromCookie(cookie: HttpCookie): URI {
        return try {
            URI(if (cookie.secure) "https" else "http", cookie.domain, cookie.path, null)
        } catch (e: Exception) {
            URI("http", cookie.domain, "/", null)
        }
    }

    private fun getCookiesForHost(host: String): List<HttpCookie> {
        val cookies = mutableListOf<HttpCookie>()
        var domain = host

        while (domain.contains(".")) {
            cookieMap.forEach { (uri, cookieList) ->
                if (uri.host == domain) {
                    cookies.addAll(cookieList)
                }
            }
            domain = domain.substringAfter(".")
        }
        return cookies
    }

    companion object {
        private const val COOKIE_PREFS = "cookie_preferences"
    }
}

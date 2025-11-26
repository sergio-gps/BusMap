package com.example.mimapa.util

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureSessionManager {

    private const val PREF_NAME = "secure_session_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val MASTER_KEY_ALIAS = "com.example.mimapa.util.SecureSessionManager.MasterKeyAlias"

    /**
     * Obtiene la MasterKey para encriptar las preferencias.
     */
    private fun getMasterKey(context: Context) = MasterKey.Builder(context.applicationContext, MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    /**
     * Obtiene las SharedPreferences encriptadas.
     */
    private fun getSharedPreferences(context: Context) = EncryptedSharedPreferences.create(
        context.applicationContext,
        PREF_NAME,
        getMasterKey(context.applicationContext), // Pasar contexto
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Guarda el token de autenticación en las SharedPreferences.
     */
    fun saveAuthToken(context: Context, token: String, email: String) {
        getSharedPreferences(context).edit {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_EMAIL, email)
        }
    }

    /**
     * Obtiene el token de autenticación de las SharedPreferences.
     */
    fun getAuthToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_AUTH_TOKEN, null)
    }

    /**
     * Obtiene el email de las SharedPreferences.
     */
    fun getEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_USER_EMAIL, null)
    }

    /**
     * Elimina el token de autenticación de las SharedPreferences.
     */
    fun clearAuthToken(context: Context) {
        getSharedPreferences(context).edit {
            remove(KEY_AUTH_TOKEN)
        }
    }
}
package com.hesabe.hesabe_payment_kotlin.aes

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object HesabeCrypt {

    fun encrypt(result: String, key: String, iv: String): String? {
        return try {

            val ivSpec = IvParameterSpec(iv.toByteArray())
            val keySpec = SecretKeySpec(key.toByteArray(), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val bytes = cipher.doFinal(result.toByteArray())
            bytes.toHexString()
        } catch (e: Exception) {
            null
        }
    }

    fun decrypt(code: String, key: String, iv: String): ByteArray? {
        val ivSpec = IvParameterSpec(iv.toByteArray())
        val keySpec = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")

        if (code.isEmpty()) throw java.lang.Exception("Empty string")

        val decrypted: ByteArray
        decrypted = try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            cipher.doFinal(hexStringToByteArray(code))
        } catch (e: java.lang.Exception) {
            throw java.lang.Exception("[decrypt] " + e.message)
        }
        return decrypted
    }

    private fun ByteArray.toHexString() = joinToString("") {
        (0xFF and it.toInt()).toString(16).padStart(2, '0')
    }

    private fun hexStringToByteArray(str: String?): ByteArray? {
        return when {
            str == null -> {
                null
            }
            str.length < 2 -> {
                null
            }
            else -> {
                val len = str.length / 2
                val buffer = ByteArray(len)
                for (i in 0 until len) {
                    buffer[i] = str.substring(i * 2, i * 2 + 2).toInt(16).toByte()
                }
                buffer
            }
        }
    }
}
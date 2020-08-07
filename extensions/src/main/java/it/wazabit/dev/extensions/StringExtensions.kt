package it.wazabit.dev.extensions

import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun String.toDateFormat(inputFormat:String,outputFormat:String,locale: Locale? = null): String {
    val date = SimpleDateFormat(inputFormat, locale ?: Locale.getDefault()).parse(this) ?: error("Invalid date format")
    return SimpleDateFormat(outputFormat, Locale.ITALIAN).format(date)
}


@Throws(SignatureException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
fun String.hmac(key:String,algorithm:String = "HmacSHA1"): String {
    val mac = Mac.getInstance(algorithm)
    mac.init(SecretKeySpec(key.toByteArray(),algorithm))
    val hashed = mac.doFinal(this.toByteArray())
    return Formatter().run {
        hashed.forEach {
            format("%02x", it)
        }
        toString()
    }
}

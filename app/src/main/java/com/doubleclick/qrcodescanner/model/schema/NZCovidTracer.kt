package com.doubleclick.qrcodescanner.model.schema
import com.doubleclick.qrcodescanner.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.doubleclick.qrcodescanner.extension.removePrefixIgnoreCase
import com.doubleclick.qrcodescanner.extension.startsWithIgnoreCase
import org.apache.commons.codec.binary.Base64
import org.json.JSONException
import org.json.JSONObject
class NZCovidTracer(
    val title: String? = null,
    val addr: String? = null,
    private val decodedBytes: String? = null
) : Schema {
    companion object {
        private const val PREFIX = "NZCOVIDTRACER:"
        fun parse(text: String): NZCovidTracer? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }
            val title: String?
            var addr: String?
            val decodedBytes: String?
            try {
                decodedBytes = String(Base64().decode(text.removePrefixIgnoreCase(PREFIX)))
            }
            catch (e: Exception) {
                return null
            }
            try {
                val obj = JSONObject(decodedBytes)
                title = obj.getString("opn")
                addr = obj.getString("adr")
            }
            catch (e: JSONException) {
                return null
            }
            addr = addr.replace("\\n", "\n")
            return NZCovidTracer(title.trim(), addr.trim())
        }
    }
    override val schema = BarcodeSchema.NZCOVIDTRACER
    override fun toFormattedText(): String = listOf(title, addr).joinToStringNotNullOrBlankWithLineSeparator()
    override fun toBarcodeText(): String = "$PREFIX$decodedBytes"
}
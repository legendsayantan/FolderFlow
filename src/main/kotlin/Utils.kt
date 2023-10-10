
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.log10

open class Utils {
    var startTime = 1L
    var total = 0L
    var progress = 0L
        set(value) {
            field = value
            if(value!=0L && lastUpdated<(System.currentTimeMillis()/100))printProgress(startTime,total,value)
            else if (value!=0L && value==total)printProgress(startTime,total,value)
        }
    fun countFiles(folder: File): Long {
        var count = 0L
        folder.listFiles()?.forEach {
            if (it.isDirectory) {
                count += countFiles(it)
            } else {
                count++
            }
        }
        return count
    }
    companion object{
        private var lastUpdated = 0L
        private fun printProgress(startTime: Long, total: Long, current: Long) {
            lastUpdated = System.currentTimeMillis()/100L
            val eta = if (current == 0L) 0 else (total - current) * (System.currentTimeMillis() - startTime) / current
            val etaHms = if (current == 0L) "N/A" else String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1)
            )
            val string = StringBuilder(140)
            val percent = (current * 100 / total).toInt()
            val halfPercent = percent / 2
            string
                .append('\r')
                .append(
                    java.lang.String.join(
                        "", Collections.nCopies(
                            if (percent == 0) 2 else 2 - log10(percent.toDouble())
                                .toInt(), " "
                        )
                    )
                )
                .append(String.format(" %d%% [", percent))
                .append(java.lang.String.join("", Collections.nCopies(halfPercent, "=")))
                .append('>')
                .append(java.lang.String.join("", Collections.nCopies(50 - halfPercent, " ")))
                .append(']')
                .append(
                    java.lang.String.join(
                        "", Collections.nCopies(
                            log10(total.toDouble()).toInt() - log10(current.toDouble())
                                .toInt(), " "
                        )
                    )
                )
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms))
            print(string)
        }
        fun calculateSHA256Checksum(file: File): String {
            val buffer = ByteArray(8192) // You can adjust the buffer size as needed.
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                BufferedInputStream(fis).use { bis ->
                    var bytesRead: Int
                    while (bis.read(buffer).also { bytesRead = it } != -1) {
                        digest.update(buffer, 0, bytesRead)
                    }
                }
            }
            return bytesToHex(digest.digest())
        }
        private fun bytesToHex(bytes: ByteArray): String {
            val hexChars = CharArray(bytes.size * 2)
            for (i in bytes.indices) {
                val v = bytes[i].toInt() and 0xFF
                hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
                hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
            }
            return String(hexChars)
        }
    }

}
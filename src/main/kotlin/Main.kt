import com.google.gson.Gson
import data.FileHash
import data.Flow
import data.FolderData
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.net.InetAddress
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit


fun main(args: Array<String>) {
    if (args.isEmpty() || args[0] == "-h" || args[0] == "--help") {
        showHelp()
        return
    }
    if (args[0] == "-v" || args[0] == "--version") {
        showVersion()
        return
    }
    if (args.size < 2) {
        println("Error: Missing flow-path.")
        return
    }
    if (!args[1].endsWith(".json")) {
        println("Error: Invalid path to the flow file.")
        return
    }
    if (args[0] == "-x" || args[0] == "--execute") {
        //execute(args[1], args[2])
        return
    }
    if (args.size < 3) {
        println("Error: Missing target folder.")
        return
    }
    if (args[0] == "-n" || args[0] == "--new") {
        Thread {
            try {
                val creator = FlowCreator()
                creator.onProgress.add { progress, total ->
                    printProgress(creator.startTime, total.toLong(), progress.toLong())
                }
                creator.create(args[1], args[2],args.contains("-o")||args.contains("--overwrite"))
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }.start()
        return
    }
    if (args[0] == "-s" || args[0] == "--sync") {
        //upload(args[1], args[2])
        return
    }


}
private fun printProgress(startTime: Long, total: Long, current: Long) {
    val eta = if (current == 0L) 0 else (total - current) * (System.currentTimeMillis() - startTime) / current
    val etaHms = if (current == 0L) "N/A" else String.format(
        "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1)
    )
    val string = StringBuilder(140)
    val percent = (current * 100 / total).toInt()
    string
        .append('\r')
        .append(
            java.lang.String.join(
                "", Collections.nCopies(
                    if (percent == 0) 2 else 2 - Math.log10(percent.toDouble())
                        .toInt(), " "
                )
            )
        )
        .append(String.format(" %d%% [", percent))
        .append(java.lang.String.join("", Collections.nCopies(percent, "=")))
        .append('>')
        .append(java.lang.String.join("", Collections.nCopies(100 - percent, " ")))
        .append(']')
        .append(
            java.lang.String.join(
                "", Collections.nCopies(
                    Math.log10(total.toDouble()).toInt() - Math.log10(current.toDouble())
                        .toInt(), " "
                )
            )
        )
        .append(String.format(" %d/%d, ETA: %s", current, total, etaHms))
    print(string)
}

fun showHelp() {
    println("FolderFlow - A simple tool to sync folders between computers, via removable drive.")
    println("Usage: java -jar FolderFlow.jar [options] [path/flow-file.json] [folder] [extras]")
    println("Options:")
    println("  -h, --help\t\t\tShow this help message and exit.")
    println("  -v, --version\t\t\tShow version information and exit.")
    println("  -n, --new \t\t\tCreate new flow configuration from target folder.")
    println("  -s, --sync\t\t\tSync target folder to flow as patch.")
    println("  -x, --execute\t\t\tApply the flow to local folder.")
    println("Extras:")
    println("  -o, --overwrite\t\tOverwrite existing flow file.")
}

fun showVersion() {
    println("FolderFlow version 0.1.0-beta")
}



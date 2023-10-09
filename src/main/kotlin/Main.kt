import com.google.gson.Gson
import data.Flow
import java.io.FileInputStream
import java.util.*


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
    if (args[0] == "-i" || args[0] == "--info") {
        Thread {
            try {
                showInfo(args[1])
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }.start()
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
                Creator().writeTo(args[1], args[2],args.contains("-o")||args.contains("--overwrite"))
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }.start()
        return
    }
    if (args[0] == "-c" || args[0] == "--compare") {
        Thread{
            try {
                Updater().compare("testflow.json","C:\\adb",args.contains("-l")||args.contains("--list"),args.contains("-np")||args.contains("--nopatch"))
            }catch (e:Exception){
                println("Error: ${e.message}")
            }
        }.start()
        return
    }
}

fun showHelp() {
    println("FolderFlow - A simple tool to sync folders between computers, via removable drive.")
    println("Usage: java -jar FolderFlow.jar [options] [drive:\\path\\flow.json] [target-folder] [extras]")
    println("Options:")
    println("  -h, --help\t\t\tShow this help message and exit.")
    println("  -v, --version\t\t\tShow version information and exit.")
    println("  -n, --new \t\t\tCreate new flow configuration from target folder.")
    println("  -i, --info\t\t\tDisplay info of a selected flow file.")
    println("  -c, --compare\t\t\tCompare target folder to flow and generate patch.")
    println("  -x, --execute\t\t\tApply the patch to local folder.")
    println("Extras:")
    println("  -o, --overwrite\t\tOverwrite (existing flow file/patch folder).")
    println("  -l, --list\t\t\tList the changes.")
    println("  -np, --nopatch\t\t\tDisable patch generation.")
}

fun showVersion() {
    println("FolderFlow version 0.1.0-beta\nMade by legendsayantan")
}

fun showInfo(path:String){
    val fis = FileInputStream(path);
    val flow = Gson().fromJson(fis.readAllBytes().toString(Charsets.UTF_8),Flow::class.java)
    fis.close()
    println("Flow Info:")
    println("  Target device: ${flow.device}")
    println("  Creation Time: ${Date(flow.time)}")
    println("  Target folder : ${flow.path}")
    println("  Patch file : ${flow.patch}")
    println("  Total files: ${flow.fileCount}")
}



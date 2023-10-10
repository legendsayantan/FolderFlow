import Utils.Companion.readContent
import com.google.gson.Gson
import data.Flow
import java.io.File
import java.util.*


fun main(args: Array<String>) {
    showIntro()
    if (args.isEmpty() || args[0] == "-h" || args[0] == "--help") {
        showHelp()
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
        Thread {
            try {
                FlowExecutor().applyPatch(args[1], args.contains("-nd") || args.contains("--nodelete"))
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }.start()
        return
    }
    if (args.size < 3) {
        println("Error: Missing target folder.")
        return
    }
    if (args[0] == "-n" || args[0] == "--new") {
        Thread {
            try {
                FlowCreator().writeTo(args[1], args[2], args.contains("-o") || args.contains("--overwrite"))
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }.start()
        return
    }
    if (args[0] == "-c" || args[0] == "--compare") {
        var customPatchPath = null as String?
        if (args.contains("-o") || args.contains("--overwrite")) {
            println("Where to create patch folder: ")
            customPatchPath = readln()
        }
        Thread {
            try {
                FlowUpdater().compare(
                    args[1],
                    args[2],
                    list = args.contains("-l") || args.contains("--list"),
                    nopatch = args.contains("-np") || args.contains("--nopatch"),
                    customPatchPath
                )
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }.start()
        return
    }
    readln()
}

fun showHelp() {
    println("A simple tool to sync folders between computers, via removable drive.")
    println("Usage: FolderFlow [options] [drive:\\path\\flow.json] [target-folder] [extras]")
    println("Options:")
    println("  -h, --help\t\t\tShow this help message and exit.")
    println("  -n, --new \t\t\tCreate new flow configuration from target folder.")
    println("  -i, --info\t\t\tDisplay info of a selected flow file.")
    println("  -c, --compare\t\t\tCompare and copy diff as patch.")
    println("  -x, --execute\t\t\tApply the patch to local folder.")
    println("Extras:")
    println("  -o, --overwrite\t\tOverwrite (existing flow file/patch folder path).")
    println("  -l, --list\t\t\tList the changes.")
    println(" -np, --nopatch\t\t\tDisable patch generation.")
    println(" -nd, --nodelete\t\tDisable file deletion from patch.")
    println("Examples:")
    println("  FolderFlow -n E:\\flow.json C:\\oldFolder")
    println("  FolderFlow -c E:\\flow.json D:\\updatedFolder -l")
    println("  FolderFlow -x E:\\flow.json")
    println("Find more info at https://github.com/legendsayantan/FolderFlow")
}

fun showIntro() {
    println("FolderFlow v0.1.0-beta")
}

fun showInfo(path: String) {
    val content = File(path).readContent()
    val flow = Gson().fromJson(content.toString(), Flow::class.java)
    println("Flow Info:")
    println("  Target device: ${flow.device}")
    println("  Creation Time: ${Date(flow.time)}")
    println("  Target folder : ${flow.path}")
    println("  Total files: ${flow.fileCount}")
    if (flow.patch != null) {
        println("  Patch folder : ${flow.patch}")
        println("  Files changed: ${Utils().countFiles(File(flow.patch!!))}")
    }
    if(flow.toDelete.isNotEmpty())
        println("  Files deleted: ${flow.toDelete.size}")
}



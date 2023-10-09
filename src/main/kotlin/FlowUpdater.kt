import com.google.gson.Gson
import data.Flow
import data.FolderData
import java.io.File
import java.io.FileOutputStream

class FlowUpdater : Utils() {
    private val flowCreator = FlowCreator()
    fun compare(
        flowPath: String,
        targetPath: String,
        list: Boolean = false,
        nopatch: Boolean = false,
        customPatch: String? = null
    ) {
        val flowFile = File(flowPath).absoluteFile
        val flowData = Gson().fromJson(flowFile.readText(), Flow::class.java)
        val currentFlowData = flowCreator.create(targetPath)
        total = currentFlowData.fileCount
        progress = 0
        startTime = System.currentTimeMillis()
        println("\nComparing target folder...")
        val difference = generateDiff(currentFlowData.root, targetPath, flowData.root, flowData.path)
        val deleted = generateDeleteList(currentFlowData.root, targetPath, flowData.root, flowData.path)
        println("\n${difference.size} files were changed. ${deleted.size} files were deleted.")
        if (list) printList("Changed files - ",difference)
        if (list) printList("Deleted files - ",deleted)
        if (nopatch || (difference.size == 0 && deleted.size == 0)) return
        val patchPath =
            customPatch ?: (flowFile.parentFile.absolutePath + "\\" + flowFile.nameWithoutExtension + "-patch")
        generatePatchFolder(flowPath, targetPath, difference, patchPath)
        val fos = FileOutputStream(flowPath)
        flowData.patch = patchPath
        flowData.toDelete.addAll(deleted)
        fos.write(Gson().toJson(flowData).toByteArray())
        fos.close()
    }

    private fun generateDiff(
        current: FolderData,
        currentParent: String,
        flow: FolderData?,
        flowParent: String
    ): ArrayList<String> {
        val diff = arrayListOf<String>()
        current.files.forEach { currentFile ->
            val equivalent = flow?.files?.find { it.name == currentFile.name }
            if (equivalent == null
                || equivalent.size != currentFile.size
                || equivalent.hash != currentFile.hash
            ) {
                diff.add("$currentParent\\${currentFile.name}")
            } else {
                //same file
            }
            progress++
        }
        current.folders.forEach { currentFolder ->
            diff.addAll(
                generateDiff(
                    currentFolder,
                    "$currentParent\\${currentFolder.name}",
                    flow?.folders?.find { it.name == currentFolder.name },
                    "$flowParent\\${currentFolder.name}"
                )
            )
        }
        return diff
    }

    private fun printList(string:String,paths: ArrayList<String>) {
        println(string)
        paths.forEach {
            println("  $it")
        }
    }

    private fun generatePatchFolder(
        flowPath: String,
        targetFolder: String,
        paths: ArrayList<String>,
        folderName: String
    ) {
        println("Generating patch...")
        val patchRoot = File(folderName)
        patchRoot.mkdirs()
        total = paths.size.toLong()
        progress = 0
        startTime = System.currentTimeMillis()
        paths.forEach { entry ->
            val targetFile = File(entry)
            val patchFile = File(entry.replace(targetFolder, patchRoot.absolutePath))
            patchFile.parentFile.mkdirs()
            patchFile.createNewFile()
            targetFile.copyTo(patchFile, true)
            progress++
        }
    }

    private fun generateDeleteList(
        current: FolderData?,
        currentParent: String,
        flow: FolderData,
        flowParent: String
    ): ArrayList<String> {
        val deleted = arrayListOf<String>()
        flow.files.forEach { currentFile ->
            if (current?.files?.find { it.name == currentFile.name }==null) {
                deleted.add("$flowParent\\${currentFile.name}")
            } else {
                //same file
            }
        }
        flow.folders.forEach { currentFolder ->
            deleted.addAll(
                generateDeleteList(
                    current?.folders?.find { it.name == currentFolder.name },
                    "$currentParent\\${currentFolder.name}",
                    currentFolder,
                    "$flowParent\\${currentFolder.name}"
                )
            )
        }
        return deleted
    }
}
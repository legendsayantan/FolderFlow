import com.google.gson.Gson
import data.Flow
import data.FolderData
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class Updater : Utils() {
    val creator = Creator()
    fun compare(
        flowPath: String,
        targetPath: String,
        list: Boolean = false,
        nopatch: Boolean = false,
        customPatch: String? = null
    ) {
        val flowData = Gson().fromJson(File(flowPath).readText(), Flow::class.java)
        val currentFlowData = creator.create(targetPath)
        total = currentFlowData.fileCount
        progress = 0
        startTime = System.currentTimeMillis()
        println("\nComparing target folder...")
        val difference = generateDiff(currentFlowData.root, targetPath, flowData.root, flowData.path)
        println("\n${difference.size} files need to be updated.")
        if (list) printDiff(difference)
        if (nopatch) return
        generatePatch(flowPath, targetPath, difference, customPatch)
        val fos = FileOutputStream(flowPath)
        flowData.patch = customPatch ?: (flowData.path + "-patch")
        fos.write(Gson().toJson(flowData).toByteArray())
        fos.close()
    }

    private fun generateDiff(
        current: FolderData,
        currentParent: String,
        flow: FolderData?,
        flowParent: String
    ): HashMap<String, String> {
        val diff = hashMapOf<String, String>()
        current.files.forEach { currentFile ->
            if (flow == null
                || flow.files.find { it.name == currentFile.name } == null
                || flow.files.find { it.name == currentFile.name }!!.size != currentFile.size
                || flow.files.find { it.name == currentFile.name }!!.hash != currentFile.hash
            ) {
                diff["$currentParent\\${currentFile.name}"] = "$flowParent\\${currentFile.name}"
            } else {
                //same file
            }
            progress++
        }
        current.folders.forEach { currentFolder ->
            diff.putAll(
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

    private fun printDiff(hashMap: HashMap<String, String>) {
        println("Files are -")
        hashMap.forEach {
            println("  ${it.key}\n\t-> ${it.value}")
        }
    }

    private fun generatePatch(
        flowPath: String,
        targetFolder: String,
        hashMap: HashMap<String, String>,
        folderName: String? = null
    ) {
        println("Generating patch...")
        val flowFile = File(flowPath).absoluteFile
        val patchRoot = File(flowFile.parent, folderName ?: (flowFile.nameWithoutExtension + "-patch"))
        patchRoot.mkdirs()
        total = hashMap.size.toLong()
        progress = 0
        startTime = System.currentTimeMillis()
        hashMap.forEach { entry ->
            val targetFile = File(entry.key)
            val patchFile = File(entry.key.replace(targetFolder, patchRoot.absolutePath))
            patchFile.parentFile.mkdirs()
            patchFile.createNewFile()
            targetFile.copyTo(patchFile, true)
            progress++
        }
    }

    fun copyFileDataByOutputStream(fileToCopy: File, destinationFileOutputStream: OutputStream) {
        val fileInputStream = fileToCopy.inputStream()
        try {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = fileInputStream.read(buffer)
            while (bytes >= 0) {
                destinationFileOutputStream.write(buffer, 0, bytes)
                bytes = fileInputStream.read(buffer)
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            destinationFileOutputStream.flush()
            destinationFileOutputStream.close()
            fileInputStream.close()
        }
    }
}
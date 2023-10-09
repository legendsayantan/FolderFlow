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
import kotlin.system.exitProcess

class FlowCreator : Utils(){
    fun writeTo(flow: String, target: String, overwrite:Boolean = false) {
        val file = File(flow)
        if (file.exists() && overwrite.not()) {
            System.err.println("Error: File already exists.")
            exitProcess(1)
        }
        val flowData = Gson().toJson(create(target))
        if(file.absoluteFile.parent!=null){
            File(file.absoluteFile.parent).mkdirs()
        }
        if(file.exists().not())file.createNewFile()
        val fos = FileOutputStream(file)
        fos.write(flowData.toByteArray())
        fos.close()
        print("\nNew flow config was created ($flow)\n")
    }

    fun create(target: String):Flow{
        print("\nCounting Files at target folder...")
        total = countFiles(File(target))
        progress = 0
        startTime = System.currentTimeMillis()
        print("\r$total files found at target folder.\nScanning files...\n")
        return Flow(
            InetAddress.getLocalHost().hostName,
            System.currentTimeMillis(),
            target,
            prepareFolder(File(target)),
            total
        )
    }

    private fun prepareFile(file: File): FileHash {
        val fis = FileInputStream(file)
        val content = fis.readAllBytes()
        fis.close()
        val hash = MessageDigest.getInstance("SHA-256").digest(content)
        val checksum = BigInteger(1, hash).toString(16)
        progress++
        return FileHash(file.name, file.length(), checksum)
    }

    private fun prepareFolder(folder: File): FolderData {
        val files = mutableListOf<FileHash>()
        val folders = mutableListOf<FolderData>()
        folder.listFiles()?.forEach {
            if (it.isDirectory) {
                folders.add(prepareFolder(it))
            } else {
                files.add(prepareFile(it))
            }
        }
        return FolderData(folder.name, files, folders)
    }
}
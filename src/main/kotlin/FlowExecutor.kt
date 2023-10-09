import com.google.gson.Gson
import data.Flow
import java.io.File

class FlowExecutor : Utils(){
    fun applyPatch(flowPath: String,noDelete: Boolean = false) {
        val flowFile = File(flowPath)
        val flowData = Gson().fromJson(flowFile.readText(), Flow::class.java)
        val patchPath = flowData.patch
        if(patchPath==null && flowData.toDelete.isEmpty()){
            println("This flow wasn't compared with an updated version of the folder. Terminating...")
            return
        }
        if(patchPath!=null){
            println("\nPatching files...")
            total = countFiles(File(patchPath))
            progress = 0
            startTime = System.currentTimeMillis()
            patchFolder(patchPath, flowData.path)
        }
        if(!noDelete){
            total = flowData.toDelete.size.toLong()
            progress = 0
            startTime = System.currentTimeMillis()
            println("\nDeleting old files...")
            flowData.toDelete.forEach {
                File(it).delete()
                progress++
            }
        }
        println("\nCompletely patched.")
        launchPostActions(flowPath, flowData, flowFile)
    }
    private fun patchFolder(patchPath: String, currentParent: String) {
        File(patchPath).listFiles()?.forEach { file ->
            if (file.isDirectory) {
                patchFolder(file.absolutePath, "$currentParent\\${file.name}")
            } else {
                val targetFile = File("$currentParent\\${file.name}")
                if (targetFile.exists()) {
                    targetFile.delete()
                }
                file.copyTo(targetFile)
            }
            progress++
        }
    }
    private fun launchPostActions(flowPath: String, flowData: Flow, flowFile: File){
        while (true){
            println("Choose how to continue: ")
            println("1. Regenerate the flow file.")
            println("2. Delete the complete flow.")
            println("3. Delete patch files only.")
            println("4. Exit.")
            when(readln()){
                "1"->{
                    FlowCreator().writeTo(flowPath, flowData.path,true)
                }
                "2"->{
                    flowData.patch?.let { File(it).deleteRecursively() }
                    flowFile.delete()
                    return
                }
                "3"->{
                    flowData.patch?.let { File(it).deleteRecursively() }
                }
                "4"->{
                    return
                }
                else->{
                    println("Invalid input.")
                }
            }
        }
    }
}
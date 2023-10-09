package data

data class Flow(
    val device: String,
    val time: Long,
    val path: String,
    val root: FolderData,
    val fileCount: Long,
    var patch: String? = null,
    var toDelete: ArrayList<String> = arrayListOf()
)


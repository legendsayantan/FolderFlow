package data

data class Flow(val device: String, val time: Long, val path:String, val root:FolderData, val patchPath:String? = null)

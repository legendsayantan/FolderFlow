package data

data class FolderData(val name:String, val files:List<FileHash>, val folders:List<FolderData>)

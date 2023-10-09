fun main(){
    when(1){
        0->{
            Creator().writeTo("testflow.json", "C:\\Users\\Admin\\AppData\\Local\\Android\\Sdk\\platform-tools",true)
        }
        1->{
            Thread{
                Updater().compare("testflow.json","C:\\adb")
            }.start()
        }
    }
}


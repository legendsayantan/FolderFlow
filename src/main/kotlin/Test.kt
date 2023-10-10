fun main(){
    when(readln()){
        "0"->{
            FlowCreator().writeTo("H:\\testflow.json", "F:\\test",true)
        }
        "1"->{
            Thread{
                FlowUpdater().compare("H:\\testflow.json","F:\\test2", list = true)
            }.start()
        }
        "2"->{
            Thread{
                FlowExecutor().applyPatch("H:\\testflow.json")
            }.start()
        }
    }
}


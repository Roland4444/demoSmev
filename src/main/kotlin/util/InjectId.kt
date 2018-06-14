package util
import java.io.File
import java.io.FileWriter
import java.nio.file.Paths
import java.nio.file.Files
class InjectId() {
    fun inject(input:String):String{
        var result:String=""
        val pStart=input.indexOf("MessageID>")
        if (pStart==-1) return input
        for (i in 0..pStart+9)
            result+=input.get(i)
        var offset=0
        while (input.get(pStart+offset)!='<') offset++
        result+=timeBasedUUID().generate()
        for (i in pStart+offset..input.length-1)
            result+=input.get(i)
        return result
    }

    fun inject(input:String, output:String){
        val lines = Files.readAllLines(Paths.get(input))
        val wr = FileWriter(output)
        File(input).readLines().forEach{            wr.write(inject(it).replace("\n",""))}
        wr.close()
    }

    fun getTagValue(xml: String, tagName: String): String {
        return xml.split("<"+tagName+">")[1].split("</"+tagName+">")[0];
    }

}
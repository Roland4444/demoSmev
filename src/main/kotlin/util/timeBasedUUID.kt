package util
import com.fasterxml.uuid.Generators
import java.util.UUID

class timeBasedUUID {
    var uuid: UUID?=null
    fun generate(): String {
        val timeBasedGenerator = Generators.timeBasedGenerator()
        this.uuid = timeBasedGenerator.generate()
        return uuid.toString()
    }

}

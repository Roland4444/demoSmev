package DB
import java.util.ArrayList
class buildArray {
    fun build(ip: String, db: String, user: String, pass: String): ArrayList<*> {
        val result = ArrayList<String>()
        result.add(ip)
        result.add(db)
        result.add(user)
        result.add(pass)
        return result
    }
}


package DB

import org.junit.Test

import org.junit.Assert.*
import util.InjectId
import util.SmevImportedTransForm
import util.buildSql
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileWriter
import java.sql.ResultSet
class ExecutorTest {
    @Test
    fun submit() {
        val test = SmevImportedTransForm()
        val PATH = "fetch"
        val injector = InjectId()
        org.apache.xml.security.Init.init()
        val directory = File(PATH)
        if (!directory.exists()) directory.mkdir()
        val params = ArrayList<String>()
        params.add("192.0.0.14")
        params.add("RBOOK")
        params.add("f00kbk01")
        params.add("sjrmbw1j")
        val exec = Executor(params, true)
        val result = exec.submit("select * from fns_files where f_stat='0'")
        while (result!!.next()) {
            val wr = FileWriter(PATH + File.separator + result.getInt("f_key").toString() + ".xml")
            wr.write(result.getString("f_body_xml"))
            wr.close()
            val `in` = FileInputStream(PATH + File.separator + result.getInt("f_key").toString() + ".xml")
            injector.inject(PATH + File.separator + result.getInt("f_key").toString() + ".xml", PATH + File.separator + result.getInt("f_key").toString() + "UUID.xml")
            val inppt = FileInputStream(PATH + File.separator + result.getInt("f_key").toString() + "UUID.xml")
            val out = FileOutputStream(PATH + File.separator + result.getInt("f_key").toString() + "__trans.xml")
            test.process(inppt, out)
        }
        assertNotEquals(null, result)
    }
}
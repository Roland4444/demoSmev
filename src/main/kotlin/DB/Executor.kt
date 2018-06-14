package DB
import java.sql.*
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

import java.sql.DriverManager.getConnection

class Executor {
    internal var ip: String
    internal var db: String
    internal var login: String
    internal var passwd: String
    var conn: Connection? = null
        private set

    internal var stmt: Statement? = null
    internal var res: ResultSet? = null

    @Throws(SQLException::class)
    constructor(params: ArrayList<*>) {
        this.ip = params[0].toString()
        this.db = params[1].toString()
        this.login = params[2].toString()
        this.passwd = params[3].toString()
        val connect = "jdbc:sqlserver://$ip\\MSSQLSERVER:1433;database=$db;user=$login;password=$passwd"
        conn = getConnection(connect)
    }

    @Throws(SQLException::class)
    constructor(params: ArrayList<*>, jtds: Boolean) {
        this.ip = params[0].toString()
        this.db = params[1].toString()
        this.login = params[2].toString()
        this.passwd = params[3].toString()
        val connect = "jdbc:jtds:sqlserver://$ip:1433/$db;instance=MSSQLSERVER;user=$login;password=$passwd"
        conn = getConnection(connect)
    }

    @Throws(SQLException::class)
    fun submit(query: String): ResultSet? {
        stmt = conn!!.createStatement()
        res = stmt!!.executeQuery("set concat_null_yields_null off; $query")
        println("execute   ==>set concat_null_yields_null off; $query")
        return res
    }

    @Throws(SQLException::class)
    fun getLenth(res: ResultSet): Int {
        val doubles = res
        var result = 1
        if (doubles.next()) {
            while (true) {
                if (res.getString(result) == null) break
                result++
            }
        }
        return result
    }

    fun insertBuilder(Destination: String, data: ArrayList<*>): String {
        var i = 0
        var query = "INSERT INTO $Destination VALUES("
        val pst: PreparedStatement? = null
        while (i++ < data.size) {
            if (i == data.size) {
                query += "?"
                break
            }
            query += "?,"
        }
        query += ");"
        return query
    }

    fun convertDate2String(indate: Date): String? {
        var dateString: String? = null
        val sdfr = SimpleDateFormat("yyyy-MM-dd")
        try {
            dateString = sdfr.format(indate)
        } catch (ex: Exception) {
            println(ex)
        }

        return dateString
    }

    @Throws(SQLException::class)
    fun insert(Destination: String, data: ArrayList<Any>) {
        val pst = conn!!.prepareStatement(insertBuilder(Destination, data))
        var i = 1

        for (o in data) {
            if (o is Int) {
                pst.setInt(i++, o)
            }
            if (o is String) {
                pst.setString(i++, o)
            }
            if (o is Double) {
                pst.setDouble(i++, o)
            }
        }
        pst.executeUpdate()
    }

}


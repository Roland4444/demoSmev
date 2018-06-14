package util

class buildSql(param: String) {
    var sql: String
    init {
        this.sql = "set concat_null_yields_null off; SELECT f_body_xml FROM fns_files WHERE f_key='$param';"
    }
    fun result(): String {
        return this.sql
    }
}

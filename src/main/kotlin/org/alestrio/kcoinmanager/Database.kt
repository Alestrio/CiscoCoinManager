package org.alestrio.kcoinmanager

import com.github.vokorm.getOneBy
import com.gitlab.mvysny.jdbiorm.JdbiOrm
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import org.alestrio.kcoinmanager.data.model.Setting
import org.mindrot.jbcrypt.BCrypt
import java.lang.IllegalStateException
import java.net.URI
import kotlin.collections.HashMap

class Database {
    private var settings : HashMap<String, String>

    init{
        /** Datasource
         */
        val cfg = MysqlDataSource()
        /**** Only for local testing
        cfg.setURL("jdbc:mysql://127.0.0.1:3306/ciscocoin")
        cfg.user = "alexis"
        cfg.setPassword("alexis")
         ****/
        val jdbUri: URI = URI(System.getenv("JAWSDB_URL"))

        val username: String = jdbUri.userInfo.split(":")[0]
        val password: String = jdbUri.userInfo.split(":")[1]
        val port: String = java.lang.String.valueOf(jdbUri.port)
        val jdbUrl = "jdbc:mysql://" + jdbUri.host.toString() + ":" + port + jdbUri.path

        cfg.setURL(jdbUrl)
        cfg.user = username
        cfg.setPassword(password)
        JdbiOrm.setDataSource(cfg)
        this.settings = this.fetchApplicationSettings()
    }

    private fun populateIfFirstRun() {
        /**
         * Function used to define default values for first run
         */
        try {
            Setting.getOneBy { "skey= :skey"("skey" to "admin_password") }
        }catch(ex:IllegalStateException){
            val admin_usrname = Setting(skey="admin_username", value="admin")
            val admin_password = Setting(skey="admin_password", value="admin")
            admin_usrname.save()
            admin_password.save()
        }
    }

    private fun fetchApplicationSettings(): HashMap<String, String>{
        /**
         * Function fetching and getting every setting and importing it into a hash map
         */
        val settings = Setting.findAll()
        val map = HashMap<String, String>()
        settings.forEach { setting ->
            map[setting.skey] = setting.value
        }
        return map
    }

    fun getSettingByKey(key:String):String?{
        /**
         * Getter for properties
         */
        return this.settings[key]
    }

    fun updateAdminPassword(password:String){
        val adminPwSetting = Setting.getOneBy{"skey= :skey"("skey" to "admin_password")}
        adminPwSetting.value = BCrypt.hashpw(password, BCrypt.gensalt())
        adminPwSetting.save()
    }
}
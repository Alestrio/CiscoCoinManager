package org.alestrio.kcoinmanager

import com.vaadin.flow.component.login.AbstractLogin
import org.alestrio.kcoinmanager.data.model.Setting
import org.alestrio.kcoinmanager.data.model.User
import org.mindrot.jbcrypt.BCrypt
import java.lang.NullPointerException

class LoginService {
    fun disconnect(){
        /**
         * This is the function handling the logout logic
         */
        TODO()
    }

    fun login(e: AbstractLogin.LoginEvent?, settings: Database): Boolean {
        /**
         * This is the function handling the login logic
         */
        //Admin connection
        if(e?.username.equals("admin")) {
            //first connection line, password check is then replaced by hashed password check
            return if(e?.password.equals("admin") && settings.getSettingByKey("admin_password")?.equals("admin")!!){
                Application.currentUser = User(id = null, pseudo = "ADMIN", balance = 0, password = "" )
                true
            } else if(BCrypt.checkpw(e?.password, settings.getSettingByKey("admin_password"))) {
                Application.currentUser = User(id = null, pseudo = "ADMIN", balance = 0, password = "" )
                true
            } else{
                false
            }
        }
        //Regular user connection
        else{
            return try {
                val users = User.findAll()
                val user: User?
                user = users.find { it.pseudo == e?.username }
                try {
                    if(BCrypt.checkpw(e?.password, user!!.password)){
                        Application.currentUser = user
                        true
                    } else false
                } catch (ex: NullPointerException) {
                    false
                }
            }catch (ex : IllegalStateException){
                false
            }
        }
    }
}
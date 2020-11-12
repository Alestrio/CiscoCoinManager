package org.alestrio.kcoinmanager

import com.vaadin.flow.component.UI.getCurrent
import com.vaadin.flow.component.login.AbstractLogin
import com.vaadin.flow.component.page.Page
import org.alestrio.kcoinmanager.data.model.User
import org.mindrot.jbcrypt.BCrypt
import java.lang.NullPointerException

class LoginService {
    /**
     * This is the singleton class handling the login logic.
     * It's is also the one responsible for keeping the current connected user in memory
     */
    var currentUser: User? = null

    fun disconnect(){
        /**
         * This is the function handling the logout logic
         */
        this.currentUser = null
        /*** CookieDisconnection ***/
    }

    fun login(e: AbstractLogin.LoginEvent?, settings: Database): Boolean {
        /**
         * This is the function handling the login logic
         */
        //Admin connection
        return if(e?.username.equals("admin")) {
            this.adminlogin(e, settings)
        }
        //Regular user connection
        else{
            this.userlogin(e)
        }
        /*** CookieConnection ***/
    }

    private fun adminlogin(e: AbstractLogin.LoginEvent?, settings: Database) : Boolean{
        /**
         * This is the function handling the login logic if the user is an Admin
         */
        //first connection line, password check is then replaced by hashed password check
        return if(e?.password.equals("admin") && settings.getSettingByKey("admin_password")?.equals("admin")!!){
            this.currentUser = User(id = null, pseudo = "ADMIN", balance = 0, password = "", isAdmin = true)
            true
        } else if(BCrypt.checkpw(e?.password, settings.getSettingByKey("admin_password"))) {
            this.currentUser = User(id = null, pseudo = "ADMIN", balance = 0, password = "", isAdmin = true)
            true
        } else{
            false
        }
    }

    private fun userlogin(e:AbstractLogin.LoginEvent?): Boolean{
        /**
         * This is the function handling the login logic if the user is a regular user
         */
        return try {
            val users = User.findAll()
            val user: User?
            user = users.find { it.pseudo == e?.username }
            try {
                if(BCrypt.checkpw(e?.password, user!!.password)){
                    this.currentUser = user
                    true
                } else false
            } catch (ex: NullPointerException) {
                false
            }
        }catch (ex : IllegalStateException){
            false
        }
    }

    private fun cookieLogin(user: User){

    }

    private fun cookieLogout(){

    }

    private fun cookieFetch() : User?{
        TODO()
    }


    @JvmName("getCurrentUser1")
    fun getCurrentUser(): User? {
        return if (this.currentUser == null) this.cookieFetch()
        else this.currentUser
    }
}
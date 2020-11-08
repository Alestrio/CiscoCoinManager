package org.alestrio.kcoinmanager


import com.github.mvysny.karibudsl.v10.*
import com.gitlab.mvysny.jdbiorm.JdbiOrm
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.login.AbstractLogin
import com.vaadin.flow.component.login.LoginOverlay
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.server.PWA
import org.alestrio.kcoinmanager.data.model.User
import org.mindrot.jbcrypt.BCrypt
import java.lang.NullPointerException


/**
 * This is the class which is creating the navbar and footer, and handling the changes for view.
 * It's also responsible for checking the connection state of the user.
 */
@PWA(name = "KCoinManager", shortName = "KCCM")
class Application : VerticalLayout(), RouterLayout {
    private var viewContainer: Div
    private var isConnected = false
    private var settings = Database()
    private lateinit var loginOverlay: LoginOverlay

    init{
        //Navbar definition
        horizontalLayout {
            setSizeFull()
            //Title and logo
            horizontalLayout {
                setSizeFull()
                image(src = "icons/icon.png", alt="icon"){
                    width = "4em"
                }
                label("KCoinManager")
                content { align(left, middle) }
            }
            //Navbar
            horizontalLayout {
                setSizeFull()
                content { align(center, middle) }
                /*  NAVBAR */
            }
        }.add(getLoginFormLayout())
        //View Container definition
        viewContainer = div{ setSizeFull() }
        //Footer definition
        horizontalLayout {
            label("Made by Alexis LEBEL with")
            icon(VaadinIcon.HEART)
            label("for the RT !")
        }
        this.setLoginFormListener()
        this.setDataSource()
    }

    private fun getLoginFormLayout() : HorizontalLayout {
        /**
         * Function creating the login form layout
         */
        return horizontalLayout {
            setSizeFull()
            content { align(right, top) }
            //Login overlay
            loginOverlay = LoginOverlay()
            val btn = button("Se connecter")
            btn.isVisible = true
            btn.addClickListener { loginOverlay.isOpened = true }
            loginOverlay.addShortcut(KeyShortcut(Key.ESCAPE)) {
                loginOverlay.close()
            }
        }
    }

    private fun setLoginFormListener() {
        /**
         * Listener for the login form
         */
        loginOverlay.addLoginListener { e ->
            val isAuthenticated: Boolean = appLogin(this, e)
            if (isAuthenticated) {
                navigateToMainPage()
                this.isConnected = true
            } else {
                loginOverlay.isError = true
                loginOverlay.close()
            }
        }
    }

    private fun setDataSource() {
        /** Datasource
         * This is only for pure testing,this DB exist only on my computer.
         * All of these would be moved to another file for production use sake
         */
        val cfg = MysqlDataSource()
        cfg.setURL("jdbc:mysql://127.0.0.1:3306/ciscocoin")
        cfg.user = "alexis"
        cfg.setPassword("alexis")
        JdbiOrm.setDataSource(cfg)
    }


    override fun showRouterLayoutContent(content: HasElement) {
        viewContainer.removeAll()
        viewContainer.element.appendChild(content.element)
    }

    companion object {
        /**
         * This is the Controller-Model for the Application class
         * Basically, it allows to separate the View from the Logic, without
         * being bothered by importing/declaring explicitly the Controller-Model
         */

        private fun navigateToMainPage() {
            TODO()
        }

        private fun hashPassword(password: String?):String{
            /**
             * This is the function hashing and salting the passwords
             */
            return BCrypt.hashpw(password, BCrypt.gensalt())
        }

        private fun appLogin(application: Application, e: AbstractLogin.LoginEvent?): Boolean {
            /**
             * This is the function handling the login logic
             */
            //Admin connection
            return if(e?.username.equals("admin")) {
                //first connection line, password check is then replaced by hashed password check
                if(e?.password.equals("admin") && application.settings.getSettingByKey("admin_password")?.equals("admin")!!) true
                else hashPassword(e?.password) == application.settings.getSettingByKey("admin_password")
            }
            //Regular user connection
            else{
                return try {
                    val users = User.findAll()
                    val user: User?
                    user = users.find { it.pseudo == e?.username }
                    return try {
                        (hashPassword(e?.password) == user!!.password)
                    } catch (ex: NullPointerException) {
                        false
                    }
                }catch (ex : IllegalStateException){
                    false
                }
            }
        }
    }
}


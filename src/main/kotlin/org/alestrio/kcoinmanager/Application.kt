package org.alestrio.kcoinmanager


import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.login.AbstractLogin
import com.vaadin.flow.component.login.LoginOverlay
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo
import org.mindrot.jbcrypt.BCrypt

/**
 * This is the class which is creating the navbar, and handling the changes for view.
 * It's also responsible for checking the connection state of the user.
 */
@PWA(name = "KCiscoCoinManager", shortName = "KCCM")
class Application : VerticalLayout(), RouterLayout {
    private var viewContainer: Div
    private var isConnected = false
    private var settings = Database()
    private lateinit var loginOverlay: LoginOverlay

    init{
        //Navbar definition
        horizontalLayout() {
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
            //Login Form
            horizontalLayout {
                setSizeFull()
                content { align(right, middle) }
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
        //View Container definition
        viewContainer = div{ setSizeFull() }
        //Footer definition
        horizontalLayout {
            label("Made by Alexis LEBEL with")
            icon(VaadinIcon.HEART)
            label("for the RT !")
        }
        loginOverlay.addLoginListener { e ->
            val isAuthenticated: Boolean = appLogin(e)
            if (isAuthenticated) {
                navigateToMainPage()
                this.isConnected = true
            } else {
                loginOverlay.isError = true
                loginOverlay.close()
            }
        }
    }

    private fun appLogin(e: AbstractLogin.LoginEvent?): Boolean {
        /**
         * This is the function handling the login logic
         */
        return when {
            //Admin connection
            e?.username.equals("admin") -> {
                //first connection line, password check is then replaced by hashed password check
                if(e?.password.equals("admin") && settings.getSettingByKey("admin_password")?.equals("admin")!!) true
                else hashPassword(e?.password) == settings.getSettingByKey("admin_password")
            }
            //Regular user connection
            e?.username.equals(e?.username + "_username") -> {
                hashPassword(e?.password) == settings.getSettingByKey(e?.username + "_password")
            }
            else -> false
        }
    }
    private fun hashPassword(password: String?):String{
        /**
         * This is the function hashing and salting the passwords
         */
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }


    private fun navigateToMainPage() {
        TODO()
    }


    override fun showRouterLayoutContent(content: HasElement) {
        viewContainer.removeAll()
        viewContainer.element.appendChild(content.element)
    }

}
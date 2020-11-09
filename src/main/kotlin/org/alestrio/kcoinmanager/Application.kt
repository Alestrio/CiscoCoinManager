package org.alestrio.kcoinmanager


import com.github.mvysny.karibudsl.v10.*
import com.gitlab.mvysny.jdbiorm.JdbiOrm
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource
import com.vaadin.flow.component.HasElement
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Label
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.login.AbstractLogin
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.login.LoginOverlay
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.RouterLayout
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.server.PWA
import org.alestrio.kcoinmanager.data.model.User
import org.alestrio.kcoinmanager.view.admin.AdminBalanceView
import org.alestrio.kcoinmanager.view.admin.AdminDashboard
import org.alestrio.kcoinmanager.view.admin.TransactionsView
import org.alestrio.kcoinmanager.view.admin.UserCreationView
import org.alestrio.kcoinmanager.view.user.UserDashboard
import org.alestrio.kcoinmanager.view.user.UserTransaction
import org.mindrot.jbcrypt.BCrypt
import java.lang.NullPointerException


/**
 * This is the class which is creating the navbar and footer, and handling the changes for view.
 * It's also responsible for checking the connection state of the user.
 */
@PWA(name = "CiscoCoinManager", shortName = "CCM")
class Application : VerticalLayout(), RouterLayout {
    private var generalContainer:HorizontalLayout
    private var viewContainer: Div
    private var isConnected = false
    private var settings = Database()
    private lateinit var loginOverlay: LoginOverlay
    private lateinit var loginBtn : Button
    private var navbar:HorizontalLayout = HorizontalLayout()

    init{
        //Navbar definition
        generalContainer = horizontalLayout {
            setSizeFull()
            //Title and logo
            horizontalLayout {
                setSizeFull()
                image(src = "icons/icon.png", alt="icon"){
                    width = "4em"
                }
                label("CiscoCoinManager")
                content { align(left, middle) }
            }
        }
        generalContainer.add(getLoginFormLayout())
        this.updateNavbarDefinition()
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
            val loginText = LoginI18n.createDefault()
            val header = LoginI18n.Header()
            header.title = "KCoinManager"
            header.description = "Veuillez vous connecter afin d'accéder à votre tableau de bord !"
            loginText.header = header
            loginOverlay.setI18n(loginText)
            loginBtn = button("Se connecter")
            loginBtn.isVisible = true
            loginBtn.addClickListener { loginOverlay.isOpened = true }
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
                updateBtnDefinition()
                updateNavbarDefinition()
                loginOverlay.close()
            } else {
                loginOverlay.isError = true
                loginOverlay.close()
            }
        }
    }

    private fun updateNavbarDefinition() {
        /**
         * This is the function defining and displaying the right navabr for the current connection state.
         * These are not protected in any way yet. We will protect them using the method below :
         * showRouterLayoutContent()
         */
        val adminNavbar = HorizontalLayout()
            adminNavbar.add(RouterLink("Tableau de bord", AdminDashboard::class.java))
            adminNavbar.add(RouterLink("Transactions", TransactionsView::class.java))
            adminNavbar.add(RouterLink("Portefeuilles", AdminBalanceView::class.java))
            adminNavbar.add(RouterLink("Création d'utilisateur", UserCreationView::class.java))
            adminNavbar.setSizeFull()
            adminNavbar.content { align(center, middle) }
        val userNavbar = HorizontalLayout()
            userNavbar.add(RouterLink("Tableau de bord", UserDashboard::class.java))
            userNavbar.add(RouterLink("Transactions", UserTransaction::class.java))
            userNavbar.setSizeFull()
            userNavbar.content { align(center, middle) }
        val unregisteredNavbar =  HorizontalLayout()
            unregisteredNavbar.add(Label("Vous n'êtes pas connecté."))
            unregisteredNavbar.setSizeFull()
            unregisteredNavbar.content { align(center, middle) }
        this.navbar.removeFromParent()
        this.navbar = unregisteredNavbar
        if(this.isConnected){
            if (currentUser!!.pseudo == "ADMIN") this.navbar = adminNavbar
            else this.navbar = userNavbar
        }
        this.generalContainer.addComponentAtIndex(1, this.navbar)
    }

    private fun setDataSource() {
        /** Datasource
         * This is only for pure testing,this DB exists only on my computer.
         * All of these would be moved to another file for production use sake
         */
        val cfg = MysqlDataSource()
        cfg.setURL("jdbc:mysql://127.0.0.1:3306/ciscocoin")
        cfg.user = "alexis"
        cfg.setPassword("alexis")
        JdbiOrm.setDataSource(cfg)
    }


    override fun showRouterLayoutContent(content: HasElement) {
        /**
         * This is the function responsible for displaying the views inside the viewContainer.
         * It'll also be responsible for checking if the current user has the right to access the page
         * he is requesting.
         */
        viewContainer.removeAll()
        viewContainer.element.appendChild(content.element)
    }

    private fun updateBtnDefinition() {
        /**
         * This is the function updating the button when the user logs-in
         */
        this.loginBtn.text = currentUser?.pseudo
        this.loginBtn.addClickListener { this.disconnect() }
    }

    private fun disconnect(){
        /**
         * This is the function handling a user logout
         */
        currentUser = null
        this.loginBtn.text = "Se connecter"
        this.loginBtn.addClickListener { loginOverlay.isOpened = true }
        loginOverlay.close()
    }

    private fun appLogin(application: Application, e: AbstractLogin.LoginEvent?): Boolean {
        /**
         * This is the function handling the login logic
         */
        //Admin connection
        if(e?.username.equals("admin")) {
            //first connection line, password check is then replaced by hashed password check
            return if(e?.password.equals("admin") && application.settings.getSettingByKey("admin_password")?.equals("admin")!!){
                currentUser = User(id = null, pseudo = "ADMIN", balance = 0, password = "" )
                true
            } else if(BCrypt.checkpw(e?.password, application.settings.getSettingByKey("admin_password"))) {
                currentUser = User(id = null, pseudo = "ADMIN", balance = 0, password = "" )
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
                        currentUser = user
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

    companion object {
        /**
         * This is the Controller-Model for the Application class
         * Basically, it allows to separate the View from the Logic, without
         * being bothered by importing/declaring explicitly the Controller-Model
         */

        private fun navigateToMainPage() {

        }

        var currentUser: User? = null

    }
}


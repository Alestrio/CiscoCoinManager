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
import org.alestrio.kcoinmanager.view.admin.AdminBalanceView
import org.alestrio.kcoinmanager.view.admin.AdminDashboard
import org.alestrio.kcoinmanager.view.admin.TransactionsView
import org.alestrio.kcoinmanager.view.admin.UserCreationView
import org.alestrio.kcoinmanager.view.user.UserChangePasswordView
import org.alestrio.kcoinmanager.view.user.UserDashboard
import org.alestrio.kcoinmanager.view.user.UserTransaction


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
    private var loginService:LoginService = LoginService()

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
        this.updateBtnDefinition()
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
            val isAuthenticated: Boolean = appLogin(e)
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

    private fun navigateToMainPage() {
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
            userNavbar.add(RouterLink("Changer le mot de passe", UserChangePasswordView::class.java))
            userNavbar.setSizeFull()
            userNavbar.content { align(center, middle) }
        val unregisteredNavbar =  HorizontalLayout()
            unregisteredNavbar.add(Label("Vous n'êtes pas connecté."))
            unregisteredNavbar.setSizeFull()
            unregisteredNavbar.content { align(center, middle) }
        this.navbar.removeFromParent()
        this.navbar = unregisteredNavbar
        if(this.isConnected && loginService.currentUser != null){
            if (loginService.currentUser!!.pseudo == "ADMIN") this.navbar = adminNavbar
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
        if(loginService.currentUser != null){
            this.loginBtn.text = loginService.currentUser?.pseudo
            this.loginBtn.addClickListener { this.disconnect() }
        }
        else{
            this.setLoginFormListener()
        }
    }

    private fun disconnect(){
        /**
         * This is the function handling a user logout
         */
        loginService.currentUser = null
        this.loginBtn.text = "Se connecter"
        this.updateNavbarDefinition()
        this.updateBtnDefinition()
        this.goToWelcomeView()
    }

    private fun goToWelcomeView() {
        /**
         * This is the function managing the return to welcomeview
         */
       this.ui.ifPresent{
           it.navigate("")
       }
    }

    private fun appLogin(e: AbstractLogin.LoginEvent?): Boolean {
        /**
         * This is the function handling the login call to the loginService
         */
        return loginService.login(e, settings)
    }

}
package org.alestrio.kcoinmanager.view.user

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.LoginService
import java.lang.NullPointerException

@Route("user-changepassword",layout = Application::class)
class UserChangePasswordView: VerticalLayout() {
    private val loginService:LoginService = getLoginService()
    private val currentUser = loginService.currentUser
    init {
        if(loginService.currentUser != null) {
            div {
                h1("Hey ! Pas si vite !")
                label("Vous devez changer votre mot de passe avant de continuer !")
                formLayout {
                    val newPw = textField("Nouveau mot de passe")
                    button("Valider") {
                        onLeftClick {
                            try {
                                val notHashedPw = newPw.value
                                currentUser!!.password = notHashedPw
                                currentUser.hashPassword()
                                Notification.show("Le mot de passe a bien été mis à jour !")
                            } catch (ex: NullPointerException) {
                                Notification.show("Vous n'êtes pas connecté.. Que faites-vous ici ?")
                            }
                        }
                    }
                }
            }
        }
        else{
            this.ui.ifPresent{ it.navigate("forbidden") }
        }
    }
    private fun getLoginService(): LoginService {
        var service: LoginService? = Session["loginService"] as LoginService?
        if (service == null) {
            service = LoginService()
            Session["loginService"] = service
        }
        return service
    }
}
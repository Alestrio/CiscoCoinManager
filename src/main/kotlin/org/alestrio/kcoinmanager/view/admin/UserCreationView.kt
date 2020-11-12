package org.alestrio.kcoinmanager.view.admin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.Database
import org.alestrio.kcoinmanager.LoginService
import org.alestrio.kcoinmanager.data.model.User

@Route("user-creation",layout = Application::class)
class UserCreationView : VerticalLayout(){
    private var loginService = getLoginService()
    private var user: User? = User()
        set(value){
            field = value
            if (value != null) binder.readBean(value)
        }
    private val binder = beanValidationBinder<User>()
    init {
        if(loginService.currentUser != null && loginService.currentUser!!.isAdmin) {
            div {
                h1("Ajouter un utilisateur")
                formLayout {
                    isMargin = false
                    textField("Pseudo") {
                        bind(binder).bind(User::pseudo)
                    }
                    textField("Mot de passe") {
                        bind(binder).bind(User::password)
                    }
                    label("(L'utilisateur devra modifier son mot de passe à la première utilisation de l'application)")
                    button("Créer l'utilisateur") {
                        onLeftClick {
                            val user = user!!
                            if (binder.validate().isOk && binder.writeBeanIfValid(user)) {
                                user.hashPassword()
                                user.save()
                                Notification.show("L'utilisateur a été créé !")
                            }
                        }
                    }
                }
                h1("Changer le mot de passe administrateur")
                formLayout {
                    val newPw = textField("Nouveau mot de passe")
                    button("Valider") {
                        onLeftClick {
                            val notHashedPw = newPw.value
                            Database().updateAdminPassword(notHashedPw)
                            Notification.show("Le mot de passe a bien été mis à jour !")
                        }
                    }
                }
            }
        }
        else {
            this.ui.ifPresent{
                it.navigate("forbidden")
                print("present")
            }
            print("forbidden")
            if(loginService.currentUser != null) print(loginService.currentUser.toString())
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
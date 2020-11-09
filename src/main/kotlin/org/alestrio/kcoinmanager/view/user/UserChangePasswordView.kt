package org.alestrio.kcoinmanager.view.user

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.alestrio.kcoinmanager.Application
import java.lang.NullPointerException

@Route("user-changepassword",layout = Application::class)
class UserChangePasswordView: VerticalLayout() {
    init {
        div {
            h1("Hey ! Pas si vite !")
            label("Vous devez changer votre mot de passe avant de continuer !")
            formLayout {
                val newPw = textField("Nouveau mot de passe")
                button("Valider") {
                    onLeftClick {
                        try{
                            val notHashedPw = newPw.value
                            Application.currentUser!!.password = notHashedPw
                            Application.currentUser!!.hashPassword()
                            Notification.show("Le mot de passe a bien été mis à jour !")
                        }catch(ex : NullPointerException){
                            Notification.show("Vous n'êtes pas connecté.. Que faites-vous ici ?")
                        }

                    }
                }
            }
        }
    }
}
package org.alestrio.kcoinmanager.view.admin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.Database
import org.alestrio.kcoinmanager.data.model.User

@Route("user-creation",layout = Application::class)
class UserCreationView : VerticalLayout(){
    private var user: User? = User()
        set(value){
            field = value
            if (value != null) binder.readBean(value)
        }
    private val binder = beanValidationBinder<User>()
    init {
        div {
            h1("Ajouter un utilisateur")
            formLayout {
                isMargin  = false
                textField("Pseudo"){
                    bind(binder).bind(User::pseudo)
                }
                textField("Mot de passe") {
                    bind(binder).bind(User::password)
                }
                button("Créer l'utilisateur (celui-ci devra modifier son mot de passe à la première utilisation de l'application"){
                    onLeftClick {
                        val user = user!!
                        if(binder.validate().isOk && binder.writeBeanIfValid(user)){
                            user.hashPassword()
                            user.save()
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
                    }
                }
            }
        }
    }
}
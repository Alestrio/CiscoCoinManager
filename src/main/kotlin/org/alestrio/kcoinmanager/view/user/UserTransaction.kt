package org.alestrio.kcoinmanager.view.user

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.LoginService
import org.alestrio.kcoinmanager.data.model.Transaction
import org.alestrio.kcoinmanager.data.model.User
import java.lang.NullPointerException

@Route("user-transaction",layout = Application::class)
class UserTransaction: VerticalLayout() {
    private val loginService: LoginService = getLoginService()
    private val binder = Binder(Transaction::class.java)
    private var transac: Transaction? = Transaction()
        set(value){
            field = value
            if (value != null) binder.readBean(value)
        }
    init {
        if(loginService.currentUser != null) {
            div {
                h1("Créer une transaction")
                formLayout {
                    comboBox<String> ("Destinataire :") {
                        setItems(getAllPseudos())
                        bind(binder).bind(Transaction::destination)
                    }
                    textField ("Montant :") {
                        bind(binder).toInt().bind(Transaction::amount)
                    }
                    button("Envoyer !"){
                        onLeftClick { 
                            try {
                                val transac = transac!!
                                if (binder.validate().isOk && binder.writeBeanIfValid(transac)) {
                                    transac.source = loginService.currentUser!!.pseudo
                                    if(applyTransac(transac)){
                                        transac.save()
                                        Notification.show("La transaction a été créée !")
                                    }else{
                                        Notification.show("La transaction a échoué, avez-vous assez de CiscoCoin pour faire cette opération ?")
                                    }
                                }
                            }catch (ex : NullPointerException){
                                ex.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
        else{
            this.ui.ifPresent { it.navigate("forbidden") }
        }
    }

    private fun applyTransac(transac: Transaction): Boolean {
        try{
            val sourceUser = loginService.currentUser!!
            val destinationUser = User.findAll().find { it.pseudo == transac.destination }!!
            return if (sourceUser.balance >= transac.amount){
                sourceUser.balance -= transac.amount
                destinationUser.balance += transac.amount
                sourceUser.save()
                destinationUser.save()
                true
            }
            else{
                false
            }
        }catch (ex : NullPointerException){
            ex.printStackTrace()
            return false
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

    private fun getAllPseudos(): ArrayList<String>{
        val pseudos:ArrayList<String> = ArrayList()
        User.findAll().forEach {
            pseudos.add(it.pseudo)
        }
        return pseudos
    }
}
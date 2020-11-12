package org.alestrio.kcoinmanager.view.user

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.data.provider.DataProvider
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.LoginService
import org.alestrio.kcoinmanager.data.model.Transaction
import org.alestrio.kcoinmanager.data.model.User

@Route("user-dashboard",layout = Application::class)
class UserDashboard: VerticalLayout() {
    private val loginService: LoginService = getLoginService()
    private val currentUser: User? = loginService.currentUser
    init {
        if(currentUser != null) {
            div {
                h1("Tableau de bord :")
                verticalLayout {
                    h2("Bienvenue ${currentUser.pseudo} !")
                    h2("Votre solde de CiscoCoins est de : " + currentUser.balance)
                    h2("Voici un résumé de vos transactions :")
                }.add(getGridOfTransactions())
            }
        }
        else{
            this.ui.ifPresent { it.navigate("forbidden") }
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

    private fun getGridOfTransactions(user:User = currentUser!!):Grid<Transaction>{
        fun getOnlyUserInvolvedTransactions(): MutableList<Transaction> {
            val allTrx = Transaction.findAll()
            val invTrx: MutableList<Transaction> = ArrayList()
            allTrx.forEach {
                if(it.source == user.pseudo || it.destination == user.pseudo){
                    invTrx.add(it)
                }
            }
            return allTrx
        }
        return grid(dataProvider = DataProvider.ofCollection(getOnlyUserInvolvedTransactions())){
            flexGrow = 1.0
            addColumnFor(Transaction::id)
            addColumnFor(Transaction::source)
            addColumnFor(Transaction::amount)
            addColumnFor(Transaction::destination)
        }
    }
}
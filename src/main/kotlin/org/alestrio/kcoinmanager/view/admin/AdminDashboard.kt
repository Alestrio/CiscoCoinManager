package org.alestrio.kcoinmanager.view.admin

import com.github.mvysny.karibudsl.v10.div
import com.github.mvysny.karibudsl.v10.h1
import com.github.mvysny.karibudsl.v10.h2
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.LoginService
import org.alestrio.kcoinmanager.data.model.Transaction
import org.alestrio.kcoinmanager.data.model.User

@Route("admin-dashboard",layout = Application::class)
class AdminDashboard: VerticalLayout() {
    private val loginService: LoginService = getLoginService()
    init {
        if(loginService.currentUser != null && loginService.currentUser!!.isAdmin) {
            div {
                h1("Tableau de bord administratif :")
                h2("Nombre total de CiscoCoins en circulation : ${getTotalCirculatingCiscoCoin()}")
                h2("Nombre total de transactions : ${getTransactionsNumber()}")
                h2("Nombre de transactions aujourd'hui : ")
                h2("Nombre d'utilisateurs enregistrés : ${getUserCount()}")
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

    private fun getTotalCirculatingCiscoCoin():Int{
        var total = 0
        User.findAll().forEach { total += it.balance }
        return total
    }

    private fun getTransactionsNumber():Int{
        return Transaction.count().toInt()
    }

    private fun getTransactionsToday(){

    }

    private fun getUserCount():Int{
        return User.count().toInt()
    }
}
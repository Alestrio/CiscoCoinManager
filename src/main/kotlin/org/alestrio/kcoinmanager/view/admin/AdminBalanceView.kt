package org.alestrio.kcoinmanager.view.admin

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.LoginService
import com.vaadin.flow.data.provider.DataProvider
import org.alestrio.kcoinmanager.data.model.User

@Route("admin-balance",layout = Application::class)
class AdminBalanceView : VerticalLayout(){
    private val loginService: LoginService = getLoginService()
    init {
        if(loginService.currentUser != null && loginService.currentUser!!.isAdmin) {
            div {
                h1("Tableau des utilisateurs :")
                grid(dataProvider = DataProvider.ofCollection(User.findAll())){
                  flexGrow = 1.0
                  addColumnFor(User::pseudo)
                  addColumnFor(User::balance)
                }
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
}
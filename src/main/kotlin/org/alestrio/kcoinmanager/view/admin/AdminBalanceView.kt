package org.alestrio.kcoinmanager.view.admin

import com.github.mvysny.karibudsl.v10.div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import eu.vaadinonkotlin.vaadin10.Session
import org.alestrio.kcoinmanager.Application
import org.alestrio.kcoinmanager.LoginService

@Route("admin-balance",layout = Application::class)
class AdminBalanceView : VerticalLayout(){
    private val loginService: LoginService = getLoginService()
    init {
        if(loginService.currentUser != null && loginService.currentUser!!.isAdmin) {
            div {

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
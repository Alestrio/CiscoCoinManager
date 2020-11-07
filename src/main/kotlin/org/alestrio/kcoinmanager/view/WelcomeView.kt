package org.alestrio.kcoinmanager.view

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.alestrio.kcoinmanager.Application

@Route("",layout = Application::class)
class WelcomeView: VerticalLayout() {
    init {
        div {
            content { align(center, middle) }
            label("Bienvenue sur KCoinManager !") { textAlign = "center"}
            br()
            label("Ici, vous pouvez consulter votre solde de CiscoCoins,"){ textAlign = "center"}
            br()
            label("Envoyer ou recevoir des CiscoCoins, ou encore acheter des CiscoCoins !"){ textAlign = "center"}
            br()
            label("Vous pouvez aussi acheter des produits sur la CiscoBoutique !"){ textAlign = "center"}
        }
    }
}
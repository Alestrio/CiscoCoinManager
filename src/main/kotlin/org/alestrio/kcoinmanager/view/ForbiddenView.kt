package org.alestrio.kcoinmanager.view

import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.alestrio.kcoinmanager.Application

@Route("forbidden",layout = Application::class)
class ForbiddenView: VerticalLayout(){
    init {
        div {
            content { align(center, middle) }
            label("Vous n'avez pas la permission de consulter cette page :'(") { textAlign = "center" }
        }
    }
}
/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.flow.demo.helloworld

import com.github.vok.karibudsl.flow.*
import com.vaadin.flow.router.Route
import com.vaadin.flow.component.dependency.HtmlImport
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.BodySize

/**
 * The main view contains a button and a template element.
 */
@BodySize(width = "100vw", height = "100vh")
@HtmlImport("frontend://styles.html")
@Route("")
class MainView : VerticalLayout() {
    init {
        setSizeFull()
        h1("Welcome to Vaadin 10!")
        val template = exampleTemplate()
        button("Click me") {
            onLeftClick {
                template.setValue("Clicked!")
            }
        }
        h2("The traditional Vaadin 8 hello-world in Vaadin 10")
        val name = textField("Your name:") {
            placeholder = "Please enter your name:"
        }
        button("Click me") {
            onLeftClick {
                this@MainView.label("Thanks ${name.value}, it works!")
            }
        }
    }
}

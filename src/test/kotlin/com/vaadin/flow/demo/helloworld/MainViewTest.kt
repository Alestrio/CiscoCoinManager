package com.vaadin.flow.demo.helloworld

import com.github.mvysny.kaributesting.v10.*
import com.github.mvysny.dynatest.DynaTest
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button

/**
 * Tests the UI. Uses the Browserless testing approach as provided by the [Karibu Testing](https://github.com/mvysny/karibu-testing) library.
 */
class MainViewTest: DynaTest({
    beforeEach { MockVaadin.setup(Routes().autoDiscoverViews("com.vaadin.flow.demo")) }
    afterEach { MockVaadin.tearDown() }

    test("test greeting") {
        // MockVaadin.setup() discovered all @Routes and prepared the UI for us; we can now read components from it.
        // the root route should be initialized; let's check whether it is indeed set in the UI
        val main = UI.getCurrent().children.findFirst().get() as MainView

        // however this kind of lookups are pretty fragile. Let's use the _get() function instead.

        // simulate a button click as if clicked by the user
        _get<Button> { caption = "Click me" } ._click()

        // look up the notification and assert on its value
        expectNotifications("Clicked!")
    }
})

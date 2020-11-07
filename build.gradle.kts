import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.gretty") version "3.0.3"
    war
    id("com.vaadin") version "0.14.3.7"
}

val karibudsl_version = "0.7.5"
val vaadin_version = "14.4.2"
val vok_version = "0.9.0"

defaultTasks("clean", "build")

repositories {
    jcenter()
    maven { setUrl("https://maven.vaadin.com/vaadin-addons") }
}

gretty {
    contextPath = "/"
    servletContainer = "jetty9.4"
    // managedClassReload = true // temporarily disabled because of https://github.com/gretty-gradle-plugin/gretty/issues/166
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        // to see the exceptions of failed tests in Travis-CI console.
        exceptionFormat = TestExceptionFormat.FULL
    }
}

val staging by configurations.creating

dependencies {
    // Karibu-DSL dependency
    implementation("com.github.mvysny.karibudsl:karibu-dsl:${karibudsl_version}")

    // Vaadin 14
    implementation("com.vaadin:vaadin-core:${vaadin_version}") {
        // Webjars are only needed when running in Vaadin 13 compatibility mode
        listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
                "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
                "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
                .forEach { exclude(group = it) }
    }
    //implementation("eu.vaadinonkotlin:vok-framework-jpa:${vok_version}")
    providedCompile("javax.servlet:javax.servlet-api:3.1.0")

    // logging
    // currently we are logging through the SLF4J API to SLF4J-Simple. See src/main/resources/simplelogger.properties file for the logger configuration
    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation(kotlin("stdlib-jdk8"))
    implementation("mysql:mysql-connector-java:5.1.37")
    implementation("eu.vaadinonkotlin:vok-framework-v10-vokdb:${vok_version}")
    implementation("com.github.mvysny.vokorm:vok-orm:1.4")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("commons-io:commons-io:2.8.0")

    // test support
    testImplementation("com.github.mvysny.kaributesting:karibu-testing-v10:1.2.6")
    testImplementation("com.github.mvysny.dynatest:dynatest-engine:0.19")

    // heroku app runner
    staging("com.heroku:webapp-runner-main:9.0.36.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// Heroku
tasks {
    val copyToLib by registering(Copy::class) {
        into("$buildDir/server")
        from(staging) {
            include("webapp-runner*")
        }
    }
    val stage by registering {
        dependsOn("build", copyToLib)
    }
}

vaadin {
    if (gradle.startParameter.taskNames.contains("stage")) {
        productionMode = true
    }
    pnpmEnable = true
}

import org.gradle.api.tasks.JavaExec

plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.rickmvi"
version = "1.11.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit))
    testImplementation(libs.junitjupiter)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    implementation(libs.gson)
    implementation(libs.annotations)
    implementation(libs.yaml)

    implementation("com.h2database:h2:2.1.214")

    // ASM para manipulação de bytecode
    implementation("org.ow2.asm:asm:9.6")
    implementation("org.ow2.asm:asm-commons:9.6")
    implementation("org.ow2.asm:asm-tree:9.6")
    implementation("org.ow2.asm:asm-util:9.6")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runExample") {
    group = "application"
    description = "Executa o runner de exemplo que demonstra CRUD via JdbcTemplate"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.github.rickmvi.jtoolbox.jdbc.runner.DbExampleRunner")
}

mavenPublishing {
    coordinates(group.toString(), "jtoolbox", version.toString())

    pom {
        name.set("JToolBox")
        description.set("Framework Java de produtividade com Injeção de Dependência (IoC), Web Services simplificados e um conjunto de utilidades essenciais para desenvolvimento moderno.")
        inceptionYear.set("2025")
        url.set("https://github.com/rickmvi/JToolBox")

        licenses {
            license {
                name.set("GNU Lesser General Public License v3.0")
                url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("rickmvi")
                name.set("Rick M. Viana")
                url.set("https://github.com/rickmvi")
            }
        }

        scm {
            url.set("https://github.com/rickmvi/JToolBox")
            connection.set("scm:git:git://github.com/rickmvi/JToolBox.git")
            developerConnection.set("scm:git:ssh://git@github.com/rickmvi/JToolBox.git")
        }
    }
}
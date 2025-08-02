plugins {
    id("java")
    id("com.vanniktech.maven.publish") version "0.34.0"
}

group = "io.github.rickmvi"
version = "1.4.4"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("org.jetbrains:annotations:24.1.0")
}

tasks.test {
    useJUnitPlatform()
}

mavenPublishing {
    coordinates(group.toString(), "jtoolbox", version.toString())

    pom {
        name.set("JToolBox")
        description.set("Console and formatting utility library")
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

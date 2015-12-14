import com.beust.kobalt.file
import com.beust.kobalt.homeDir
import com.beust.kobalt.plugin.kotlin.kotlinProject
import com.beust.kobalt.plugin.packaging.assemble
import com.beust.kobalt.plugin.publish.jcenter
import com.beust.kobalt.plugins
import com.beust.kobalt.repos

import no.tornado.kobalt.plugin.ssh.ssh

val r = repos()

val pl = plugins(file(homeDir("Projects/kobalt-ssh-plugin/kobaltBuild/libs/kobalt-ssh-plugin-0.1.jar")))

val p = kotlinProject {

    name = "kobalt-ssh-plugin"
    group = "no.tornado"
    artifactId = name
    version = "0.1"

    sourceDirectories {
        path("src/main/kotlin")
        path("src/main/resources")
    }

    dependencies {
        compile("com.beust:kobalt:0.329")
        compile("com.jcraft:jsch:0.1.53")
    }

    assemble {
        mavenJars {
        }
    }

    ssh {
        name = "copySomeFile"
        username = "someuser"
        password = "somepass"
        host = "somehost"

        scp("/etc/passwd", "someremotefolder")
    }

    jcenter {
        publish = true
    }

}

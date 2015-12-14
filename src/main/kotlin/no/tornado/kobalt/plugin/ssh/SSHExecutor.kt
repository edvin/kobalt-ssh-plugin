package no.tornado.kobalt.plugin.ssh

import com.beust.kobalt.TaskResult
import com.beust.kobalt.misc.LocalProperties
import com.beust.kobalt.misc.log
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import no.tornado.kobalt.plugin.ssh.Operation.SCP
import no.tornado.kobalt.plugin.ssh.Operation.SSHExec
import javax.inject.Inject

class SSHExecutor @Inject constructor(val props: LocalProperties) {

    companion object {
        private const val PROPERTY_SSH_HOST = "ssh.host"
        private const val PROPERTY_SSH_USER = "ssh.user"
        private const val PROPERTY_SSH_PASSWORD = "ssh.password"
    }

    fun execute(config: SSHConfig): TaskResult {
        log(2, "Executing ssh task ${config.name}...")

        config.withSession {
            for (op in config.operations) {
                when (op) {
                    is SSHExec -> println("Performing sshexec on ${this}")
                    is SCP -> println("Performing scp on ${this}")
                }
            }
        }

        return TaskResult()
    }


    fun SSHConfig.withSession(closure: Session.() -> Unit) {
        val username = username ?: props.get(PROPERTY_SSH_USER)
        val password = password ?: props.get(PROPERTY_SSH_PASSWORD)
        val host = host ?: props.get(PROPERTY_SSH_HOST)

        JSch().getSession(username, host, port).apply {
            userInfo = SSHUserInfo(password)

            log(2, "Connecting to SSH host $host:$port")

            connect()

            try {
                closure.invoke(this)
            } finally {
                log(2, "Disconnecting SSH session")
                disconnect()
            }
        }
    }

}
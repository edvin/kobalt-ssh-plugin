package no.tornado.kobalt.plugin.ssh

import com.beust.kobalt.TaskResult
import com.beust.kobalt.misc.LocalProperties
import com.beust.kobalt.misc.log
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import no.tornado.kobalt.plugin.ssh.Operation.SCP
import no.tornado.kobalt.plugin.ssh.Operation.SSHExec
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SSHExecutor @Inject constructor(val props: LocalProperties) {
    private val executorService = Executors.newCachedThreadPool {
        Executors.defaultThreadFactory().newThread(it).apply {
            isDaemon = true
        }
    }

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
                    is SSHExec -> sshExec(this, op)
                    is SCP -> println("Performing scp on ${this}")
                }
            }
        }

        return TaskResult()
    }

    private fun sshExec(session: Session, op: SSHExec) {
        log(2, "Executing '${op.command}'...")

        val exec = session.openChannel("exec") as ChannelExec

        with (exec) {
            setCommand(op.command)
            connect()
        }

        try {
            val stdin = executorService.submit({ exec.inputStream.copyTo(System.out) })
            val stderr = executorService.submit({ exec.errStream.copyTo(System.out) })

            stdin.get()
            stderr.get()
        } finally {
            exec.disconnect()
        }
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
                closure(this)
            } finally {
                log(2, "Disconnecting SSH session")
                disconnect()
            }
        }
    }

}
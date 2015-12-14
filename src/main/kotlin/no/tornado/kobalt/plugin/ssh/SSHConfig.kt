package no.tornado.kobalt.plugin.ssh

import com.beust.kobalt.api.Project
import com.beust.kobalt.api.annotation.Directive

class SSHConfig(val project: Project) {
    val operations = arrayListOf<Operation>()
    var port: Int = 22
    lateinit var name: String
    var description: String? = null
    var host: String? = null
    var username: String? = null
    var password: String? = null
    var alwaysRunAfter: List<String>? = null
    var runAfter: List<String>? = null
    var runBefore: List<String>? = null

    @Directive
    fun sshexec(vararg command: String) =
            operations.addAll(command.map { Operation.SSHExec(it) })

    @Directive
    fun scp(file: String, todir: String)
        = operations.add(Operation.SCP(file, todir))
}

sealed class Operation {
    class SSHExec(val command: String) : Operation()
    class SCP(val file: String, val todir: String) : Operation()
}
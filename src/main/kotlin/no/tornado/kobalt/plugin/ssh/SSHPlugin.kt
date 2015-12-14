package no.tornado.kobalt.plugin.ssh

import com.beust.kobalt.TaskResult
import com.beust.kobalt.api.*
import com.beust.kobalt.api.annotation.Directive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SSHPlugin @Inject constructor(val executor: SSHExecutor) : BasePlugin(), ITaskContributor {
    override val name = PLUGIN_NAME

    companion object {
        const val PLUGIN_NAME = "SSH"

        const val PROPERTY_SSH_USER = "ssh.user"
        const val PROPERTY_SSH_PASSWORD = "ssh.password"
    }

    private val operations = arrayListOf<SSHConfig>()
    fun add(operation: SSHConfig) = operations.add(operation)

    override fun tasksFor(context: KobaltContext) = operations.map { config ->
        DynamicTask(
                name = config.name,
                description = config.description ?: "Run the ${config.name} SSH task",
                plugin = this,
                alwaysRunAfter = config.alwaysRunAfter ?: emptyList<String>(),
                runAfter = config.runAfter ?: emptyList<String>(),
                runBefore = config.runBefore ?: emptyList<String>(),
                closure = { project -> executor.execute(config) }
        )
    }

}

@Directive
public fun Project.ssh(init: SSHConfig.() -> Unit) {
    with(SSHConfig(this)) {
        init()
        getInstance().add(this)
    }
}

private fun getInstance() = Kobalt.findPlugin(SSHPlugin.PLUGIN_NAME) as SSHPlugin
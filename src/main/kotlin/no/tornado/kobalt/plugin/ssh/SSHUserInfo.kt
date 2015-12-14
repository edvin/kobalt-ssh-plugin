package no.tornado.kobalt.plugin.ssh

import com.jcraft.jsch.UserInfo

class SSHUserInfo(private val pass: String) : UserInfo {
    override fun getPassphrase() = null
    override fun getPassword() = pass
    override fun promptPassword(message: String) = true
    override fun promptPassphrase(message: String) = true
    override fun promptYesNo(message: String) = true
    override fun showMessage(message: String) = println(message)
}
package yakworks

import grails.plugin.rally.security.BaseUser

class BootStrap {

    def init = { servletContext ->
        BaseUser user = new BaseUser()
        user.login = "admin"
        user.email = "admin@9ci.com"
        user.name = "Admin"
        user.pass = "admin"
        user.encodePassword()
        user.persist()
    }

}

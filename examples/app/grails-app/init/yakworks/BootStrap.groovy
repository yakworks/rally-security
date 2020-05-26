package yakworks

import grails.plugin.rally.security.User

class BootStrap {

    def init = { servletContext ->
        User user = new User()
        user.login = "admin"
        user.email = "admin@9ci.com"
        user.name = "Admin"
        user.pass = "admin"
        user.encodePassword()
        user.persist()
    }

}
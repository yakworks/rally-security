package grails.plugin.rally.security

class BootStrap {

    def init = { servletContext ->
        User.withTransaction {
            User user = new User()
            user.name = "admin"
            user.login = "admin"
            user.pass = "admin"
            user.email = "admin@9ci.com"
            user.encodePassword()
            user.persist()
            assert user.id == 1

            SecRole admin = SecRole.create([name: SecRole.ADMINISTRATOR])
            SecRole power = SecRole.create([name: SecRole.POWER_USER])
            SecRole guest = SecRole.create([name: SecRole.GUEST])

            assert admin.id == 1
            assert power.id == 2
            assert guest.id == 3
        }
    }
    def destroy = {
    }
}

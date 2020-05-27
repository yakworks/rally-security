/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class BootStrap {

    def init = { servletContext ->
        User.withTransaction({
            User user = new User(name: "admin", login: "admin", email: "admin@9ci.com")
            user.pass = "admin"
            user.encodePassword()
            user.persist()
            assert user.id == 1

            SecRole admin = SecRole.create([name: SecRole.ADMINISTRATOR])
            SecRole power = SecRole.create([name: SecRole.POWER_USER])
            SecRole guest = SecRole.create([name: "Guest"])

            SecRoleUser.create(user, admin, true)

            assert admin.id == 1
            assert power.id == 2
        })
    }

}

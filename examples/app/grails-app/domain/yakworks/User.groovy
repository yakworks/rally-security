package yakworks

import grails.plugin.rally.security.BaseUser

class User extends BaseUser {

    static mapping = {
        tablePerHierarchy true
    }
}

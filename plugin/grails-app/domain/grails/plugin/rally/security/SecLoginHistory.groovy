package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class SecLoginHistory implements Serializable {

    User user
    Date loginDate
    Date logoutDate

    static mapping = {
        table 'SecLoginHistory'
        user column: 'UserId'
        version false
    }

    static constraints = {
        loginDate nullable: true
        logoutDate nullable: true
    }
}

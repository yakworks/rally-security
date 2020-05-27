/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
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

/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package grails.plugin.rally.security

import groovy.transform.EqualsAndHashCode

import grails.compiler.GrailsCompileStatic

/**
 * SecRole class for Authority.
 */
@EqualsAndHashCode(includes='name', useCanEqual=false)
@GrailsCompileStatic
class SecRole implements Serializable {

    static final String ADMINISTRATOR = "Administrator" //full access, system user
    static final String POWER_USER = "Power User"

    Boolean inactive = false

    String description
    String name

    static mapping = {
        //users column: 'secRoleId', joinTable: 'SecRoleUser'
        tablePerHierarchy false
    }

    static constraints = {
        name blank: false
        description nullable: true
    }

    static transients = ['springSecRole']

    /**
     * Spring security plugin needs all authorities to be prefixed with ROLE_ and hence all roles must
     * be saved in database with name such as ROLE_MANAGER etc. However we use custom user detail service,
     * and call getSpringSecRole when populating a authorities for the UserDetail.
     * it allows us to save role names in db without prefix ROLE_
     */
    String getSpringSecRole() {
        return "ROLE_${name}".toString()
    }
}

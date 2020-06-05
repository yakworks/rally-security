/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package grails.plugin.rally.security

import groovy.transform.CompileDynamic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import grails.compiler.GrailsCompileStatic

@gorm.AuditStamp
@GrailsCompileStatic
@EqualsAndHashCode(includes='login', useCanEqual=false)
@ToString(includes='login', includeNames=true, includePackage=false)
class BaseUser implements Serializable {

    static transients = ['pass', 'primaryRole', 'editedByName', 'org', 'enabled']

    String login
    String name
    String email
    String passwd
    Boolean mustChangePassword = false
    Date passwordChangedDate
    Boolean inactive = false

    void setEnabled(Boolean val) { inactive = !val }

    boolean getEnabled() { !inactive }

    /** temporary plain password to create a MD5 password */
    String pass = '[secret]'

    String resetPasswordToken
    Date resetPasswordDate

    static mapping = {
        cache true
        table 'Users'
        passwd column: 'password'
    }

    static constraints = {
        login blank: false, nullable: false, unique: true, maxSize: 50
        email nullable: false, blank: false, email: true, unique: true
        passwd blank: false, nullable: false, maxSize: 60, bindable: false
        passwordChangedDate nullable: true, bindable: false
        mustChangePassword bindable: false
        resetPasswordToken nullable: true
        resetPasswordDate nullable: true
    }

    SecRole getPrimaryRole() {
        if (!id) return null
        SecRoleUser.findByUser(this)?.role
    }

    @CompileDynamic
    Set<SecRole> getRoles() {
        SecRoleUser.findAllByUser(this)*.role as Set
    }

    @CompileDynamic
    void encodePassword() {
        passwd = repo.encodePassword(pass)
    }

    String getEditedByName() {
        BaseUser.get(this.editedBy)?.name
    }
}

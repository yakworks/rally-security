/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package grails.plugin.rally.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.Plugin

class RallySecurityGrailsPlugin extends Plugin {
    def grailsVersion = "3.3.10 > *"

    def title = "Rally Security"
    def author = "Your name"
    def authorEmail = ""
    def description = "Brief summary/description of the plugin."

    def documentation = "http://grails.org/plugin/rally-security"

    def loadAfter = ['spring-security-core', 'spring-security-ldap', 'spring-security-rest', 'gorm-tools', 'datasource']
    def pluginExcludes = ["**/init/**"]

    Closure doWithSpring() {
        { ->
            def securityConf = SpringSecurityUtils.securityConfig
            if (securityConf.active) {
                passwordValidator(PasswordValidator){ bean ->
                    bean.autowire = "byName"
                }
                passwordEncoder(grails.plugin.springsecurity.authentication.encoding.BCryptPasswordEncoder, 10)

                userDetailsService(RallyUserDetailsService)

                nineLoginHandler(RallyLoginHandler) { bean ->
                    bean.autowire = "byName"
                }
                rallyLogoutHandler(RallyLogoutHandler)
                authenticationDetailsSource(RallyAuthenticationDetailsSource)
            }
        }
    }
}

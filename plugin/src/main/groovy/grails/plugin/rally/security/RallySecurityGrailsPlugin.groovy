package grails.plugin.rally.security

import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.*

class RallySecurityGrailsPlugin extends Plugin {
    def grailsVersion = "3.3.10 > *"
    def pluginExcludes = []
    def title = "Rally Security"
    def author = "Your name"
    def authorEmail = ""
    def description = "Brief summary/description of the plugin."

    def documentation = "http://grails.org/plugin/rally-security"

    def loadAfter = ['spring-security-core', 'spring-security-ldap', 'spring-security-rest', 'gorm-tools', 'datasource']

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

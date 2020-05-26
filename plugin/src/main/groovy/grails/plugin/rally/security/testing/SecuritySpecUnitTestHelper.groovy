package grails.plugin.rally.security.testing

import grails.plugin.rally.security.SecService
import grails.plugin.rally.security.User
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileDynamic
import org.springframework.security.authentication.AuthenticationTrustResolverImpl
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder

@CompileDynamic
trait SecuritySpecUnitTestHelper {

    Closure doWithSpringFirst() {
        return {
            passwordEncoder(PlaintextPasswordEncoder)
            springSecurityService(SpringSecurityService) { bean ->
                bean.autowire = "byName"
            }
            authenticationTrustResolver(AuthenticationTrustResolverImpl)
            secService(SecService) { bean ->
                bean.autowire = "byName"
            }

        }
    }

    void authenticate(User user, String... roles) {
        roles = roles.collect { "ROLE_" + it}
        List authorities = AuthorityUtils.createAuthorityList(roles)

        GrailsUser grailsUser = new GrailsUser(user.login, user.passwd, user.enabled, true, !user.mustChangePassword, true, authorities, user.id)
        SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(grailsUser, user.passwd, authorities)
    }

}

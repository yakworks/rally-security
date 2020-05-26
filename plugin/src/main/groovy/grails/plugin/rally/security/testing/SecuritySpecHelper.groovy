package grails.plugin.rally.security.testing

import grails.plugin.rally.security.SecService
import grails.plugin.rally.security.User
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.testing.spock.OnceBefore
import groovy.transform.CompileDynamic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder

@CompileDynamic
trait SecuritySpecHelper {
    @Autowired
    SecService secService

    //need to name it like this, otherwise subclasses cant use setupSpec method
    @OnceBefore
    void setupSecuritySpec() {
        secService.loginAsSystemUser()
    }

    void authenticate(User user, String... roles) {
        roles = roles.collect { "ROLE_" + it}
        List authorities = AuthorityUtils.createAuthorityList(roles)

        GrailsUser grailsUser = new GrailsUser(user.login, user.passwd, user.enabled, true, !user.mustChangePassword, true, authorities, user.id)
        SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(grailsUser, user.passwd, authorities)
    }

 }

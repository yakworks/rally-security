package grails.plugin.rally.security

import groovy.transform.CompileStatic
import org.springframework.security.authentication.AuthenticationDetailsSource

import javax.servlet.http.HttpServletRequest

//Custom authentication source to create RallyAuthenticationDetails - we need this to support autologin (/login/autoLogin)
@CompileStatic
class RallyAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, RallyAuthenticationDetails> {

    @Override
    RallyAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new RallyAuthenticationDetails(request)
    }
}

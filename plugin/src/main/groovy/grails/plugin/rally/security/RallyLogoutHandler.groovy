package grails.plugin.rally.security

import grails.util.Holders
import groovy.transform.CompileDynamic
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//This is here to track users logout and can probably go away
@CompileDynamic
class RallyLogoutHandler extends SecurityContextLogoutHandler {

    @Override
    public void logout(
            final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) {
        def userService = Holders.grailsApplication.mainContext.getBean('userService')
        if (authentication) {
            userService.trackUserLogout()
        }
        super.logout(request, response, authentication)
    }

}

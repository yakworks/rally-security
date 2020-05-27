package grails.plugin.rally.security

import grails.util.Holders
import groovy.transform.CompileDynamic
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Tracks user logout
 */
@CompileDynamic
class RallyLogoutHandler extends SecurityContextLogoutHandler {

    @Override
    void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        UserService userService = Holders.grailsApplication.mainContext.getBean('userService')
        if (authentication) {
            userService.trackUserLogout()
        }
        super.logout(request, response, authentication)
    }

}

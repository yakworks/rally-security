package grails.plugin.rally.security

import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileStatic
import grails.plugin.rally.security.SecService
import grails.plugin.rally.security.User
import grails.plugin.rally.security.UserService
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AbstractAuthenticationEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent

/**
 * Springsecurity login handler
 * tracks user logins and sets flag if password expiry warning should be displayed.
 */
@CompileStatic
class RallyLoginHandler implements ApplicationListener<AbstractAuthenticationEvent> {
    UserService userService

    @Value('${grails.plugin.rally.security.password.expireEnabled:false}')
    boolean passwordExpiryEnabled

    @Value('${grails.plugin.rally.security.password.warnDays:30}')
    int passwordWarnDays

    void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            if (shouldWarnAboutPasswordExpiry((GrailsUser) event.authentication.principal)) {
                GrailsWebRequest webRequest = WebUtils.retrieveGrailsWebRequest()
                if (webRequest) {
                    webRequest.currentRequest.session['warnAboutPasswordExpiry'] = true
                }
            }
        }
        if (event instanceof InteractiveAuthenticationSuccessEvent) {
            trackLogin((InteractiveAuthenticationSuccessEvent) event)
        }
    }

    void trackLogin(InteractiveAuthenticationSuccessEvent event) {
        userService.trackUserLogin()
    }

    boolean shouldWarnAboutPasswordExpiry(GrailsUser principal) {
        boolean result = false
        User.withTransaction {
            if (passwordExpiryEnabled) {
                int warnBeforeDays = passwordWarnDays
                int remainingDays = userService.remainingDaysForPasswordExpiry(User.get((Long) principal.id))
                if (warnBeforeDays >= remainingDays) result = true
            }
        }

        return result
    }

}

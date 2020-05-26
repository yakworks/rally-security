package grails.plugin.rally.security

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j
import org.springframework.security.web.authentication.WebAuthenticationDetails

import javax.servlet.http.HttpServletRequest

/**
 * Authentication details that stores sources where this authentication comes from.
 *
 * Basically it could be "form" for standard username/password form authentication,
 * and "autologin" for authentication through autologin url.
 *
 * We mainly need this to support autoLogin (/login/autoLogin).
 * In the case of autologin, presented password is hash and should not be encrypted again when verifying passwords.
 * In the case of a regular form login, presented password will be encrypted before verifying password. @See NineAuthenticationProvider
 *
 * @see RallyAuthenticationDetails#authType
 * @see RallyAuthenticationDetails#isAutoLogin
 */
@CompileStatic
@Log4j
class RallyAuthenticationDetails extends WebAuthenticationDetails {

    public static final String TYPE_FORM = 'form'
    public static final String TYPE_AUTOLOGIN = 'autologin'

    private String authType

    /**
     * @param request that the authentication request was received from
     */
    RallyAuthenticationDetails(HttpServletRequest request) {
        super(request)
        //set authType from current request
        if (request.requestURI.substring(request.contextPath.length()).startsWith('/login/autoLogin')) {
            //log.info "NineAuthenticationDetails autologin"
            this.authType = TYPE_AUTOLOGIN
        } else {
            this.authType = TYPE_FORM
        }
    }

    boolean isAutoLogin() {
        return authType == TYPE_AUTOLOGIN
    }
}

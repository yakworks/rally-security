package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic
import grails.orm.HibernateCriteriaBuilder
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugin.springsecurity.userdetails.GrailsUser
import groovy.transform.CompileDynamic
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 * A wrapper around the key methods in springSecurityService.
 * Also contains methods that deals with security/authorization or applying common restrictions to data logged in user can see.
 *
 * non-security user related methods should go to UserServce and not here
 */
@GrailsCompileStatic
class SecService {

    SpringSecurityService springSecurityService
    AuthenticationTrustResolver authenticationTrustResolver

    /**
     * Get the currently logged in user's principal. If not authenticated and the
     * AnonymousAuthenticationFilter is active (true by default) then the anonymous
     * user's name will be returned ('anonymousUser' unless overridden).
     * calls same method on springSecurityService
     *
     * @return the principal
     */
    def getPrincipal() {
        springSecurityService.getPrincipal()
    }

    /**
     * Get the currently logged in user's <code>Authentication</code>. If not authenticated
     * and the AnonymousAuthenticationFilter is active (true by default) then the anonymous
     * user's auth will be returned (AnonymousAuthenticationToken with username 'anonymousUser'\
     * unless overridden).
     * calls same method on springSecurityService
     *
     * @return the authentication
     */
    Authentication getAuthentication() {
        springSecurityService.getAuthentication()
    }

    /**
     * Gets the currently logged in user id from principal
     */
    @CompileDynamic
    Long getUserId() {
        springSecurityService.getPrincipal()?.id?.toLong()
    }

    /**
     * Get the domain class instance associated with the current authentication.
     * calls same method on springSecurityService
     * @return the user
     */
    User getUser() {
        return (User) springSecurityService.getCurrentUser()
    }

    /**
     * Encode the password using the configured PasswordEncoder.
     * calls same method on springSecurityService
     */
    String encodePassword(String password, Object salt = null) {
        springSecurityService.encodePassword(password, salt)
    }

    /**
     * Quick check to see if the current user is logged in.
     * calls same method on springSecurityService
     * @return <code>true</code> if the authenticated and not anonymous
     */
    boolean isLoggedIn() {
        return springSecurityService.isLoggedIn()
    }

    /**
     * Verify that user has logged in fully (ie has presented username/password) and is not logged in using rememberMe cookie
     * @return
     */
    boolean isAuthenticatedFully() {
        return (isLoggedIn() && !isRememberMe())
    }

    /**
     * Rebuild an Authentication for the given username and register it in the security context.
     * Typically used after updating a user's authorities or other auth-cached info.
     * <p/>
     * Also removes the user from the user cache to force a refresh at next login.
     * calls same on springSecurityService
     *
     * @param username the user's login name
     * @param password optional
     */
    void reauthenticate(String username, String password = null) {
        springSecurityService.reauthenticate username, password
    }

    /**
     * Check if user is logged in using rememberMe cookie.
     * @return
     */
    boolean isRememberMe() {
        return authenticationTrustResolver.isRememberMe(SecurityContextHolder.context?.authentication)
    }

    private static List<GrantedAuthority> parseAuthoritiesString(String[] roleNames) {
        List<GrantedAuthority> requiredAuthorities = []
        for (String auth : roleNames) {
            auth = auth.trim()
            if (auth.length() > 0) {
                auth = "ROLE_" + auth
                requiredAuthorities.add(new SimpleGrantedAuthority(auth))
            }
        }

        return requiredAuthorities
    }

    /**
     * Check if current user has any of the specified roles
     */
    boolean ifAnyGranted(String... roles) {
        return SpringSecurityUtils.ifAnyGranted(parseAuthoritiesString(roles))
    }

    /**
     * Check if current user has all of the specified roles
     */
    boolean ifAllGranted(String... roles) {
        return SpringSecurityUtils.ifAllGranted(parseAuthoritiesString(roles))
    }

    /**
     * Check if current user has none of the specified roles
     */
    boolean ifNotGranted(String... roles) {
        return SpringSecurityUtils.ifNotGranted(parseAuthoritiesString(roles))
    }

    /**
     * Get the current user's roles.
     * @return a list of roles (empty if not authenticated).
     */
    List<String> getPrincipalRoles() {
        if (!isLoggedIn()) return []
        return user.roles*.name
    }

    /**
     * Login user with role Client.
     * Mainly used in tests, to provide a security context.
     */
    static void loginAsSystemUser() {
        User user = User.get(1)
        List<GrantedAuthority> authorities = parseAuthoritiesString([SecRole.ADMINISTRATOR] as String[])
        GrailsUser grailsUser = new GrailsUser(user.login, user.passwd, user.enabled, true, !user.mustChangePassword, true, authorities, user.id)
        SecurityContextHolder.context.authentication = new UsernamePasswordAuthenticationToken(grailsUser, user.passwd, authorities)
    }

    /**
     * Just a wrapper around ifAllGranted(SecRole.Branch), checks if logged in user has role Branch
     * @return boolean
     */
    boolean isBranchLogin() {
        return ifAllGranted(SecRole.BRANCH)
    }

}

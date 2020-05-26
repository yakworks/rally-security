package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic
import grails.core.GrailsApplication
import grails.core.support.GrailsApplicationAware
import grails.plugin.springsecurity.userdetails.GrailsUser
import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import grails.plugin.rally.security.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Slf4j
@GrailsCompileStatic
class RallyUserDetailsService implements GrailsUserDetailsService, GrailsApplicationAware {
    GrailsApplication grailsApplication
    int passwordExpireDays
    boolean passwordExpireEnabled

    @CompileDynamic
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {
        log.debug "loadUserByName(${username}, ${loadRoles})"
        User.withTransaction { status ->
            User user = User.findByLogin(username.trim())
            if (!user) {
                throw new UsernameNotFoundException("User not found: $username")
            }
            log.debug "Found user ${user} in the 9ci database"
            Boolean mustChange = user.mustChangePassword

            if (passwordExpireEnabled) {
                int expireDays = passwordExpireDays
                Date now = new Date()

                //check if user's password has expired
                if (!user.passwordChangedDate || (now - expireDays >= user.passwordChangedDate)) {
                    mustChange = true
                }

            }

            List<SimpleGrantedAuthority> authorities = []

            if (loadRoles) {
                authorities = user.roles.collect { new SimpleGrantedAuthority(it.springSecRole) }
            }

            new GrailsUser(user.login, user.passwd, user.enabled, true, !mustChange, true, authorities, user.id)
        }

    }

    /**
     * {@inheritDoc}
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(
     * java.lang.String )
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug "loadUserByName(${username})"
        loadUserByUsername username, true
    }

}

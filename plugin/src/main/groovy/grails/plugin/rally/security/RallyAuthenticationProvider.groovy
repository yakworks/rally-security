package grails.plugin.rally.security

import groovy.transform.CompileDynamic
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.authentication.encoding.PasswordEncoder
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails

/**
 * Basically a standard RepoAuthenticationProvider, that uses plaintext password encoder if authentication comes
 * from autologin url
 */
@CompileDynamic
class RallyAuthenticationProvider extends DaoAuthenticationProvider {

    PlaintextPasswordEncoder plaintextPasswordEncoder = new PlaintextPasswordEncoder()

    PasswordEncoder md5PasswordEncoder

    private boolean includeDetailsObject = false

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided")

            throw new BadCredentialsException(messages.getMessage(
                "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"),
                includeDetailsObject ? userDetails : null)
        }

        String presentedPassword = authentication.getCredentials()

        if (authentication.details instanceof RallyAuthenticationDetails && authentication.details.isAutoLogin()) {
            //comes from autologin url, should use plaintext password encoder (because
            //password hash is passed as is)
            if (!plaintextPasswordEncoder.isPasswordValid(userDetails.getPassword(), presentedPassword, null)) {
                logger.debug("Authentication failed: password does not match stored value")

                throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"),
                    includeDetailsObject ? userDetails : null)
            }
        } else if (!userDetails.password.startsWith('$2a$')) {
            //compatibility with old md5 passwords
            logger.warn("Use old MD5 based auth for user ${userDetails.username}")
            if (!md5PasswordEncoder.isPasswordValid(userDetails.getPassword(), presentedPassword, null)) {
                logger.debug("Authentication failed: password does not match stored value")

                throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"),
                    includeDetailsObject ? userDetails : null)
            }

        } else {
            super.additionalAuthenticationChecks(userDetails, authentication)
        }
    }
}

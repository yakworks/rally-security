package grails.plugin.rally.security

import grails.plugin.rally.security.SecPasswordHistory
import grails.plugin.rally.security.User
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.security.authentication.encoding.PasswordEncoder

@CompileStatic
class PasswordValidator {

    Integer passwordMinLength
    boolean passwordMustContainNumbers
    boolean passwordMustContainSymbols
    boolean passwordMustContainUpperaseLetter
    boolean passwordMustContainLowercaseLetter
    boolean passwordHistoryEnabled
    int passwordHistoryLength

    PasswordEncoder passwordEncoder

    Map validate(User user, String pass, String passConfirm) {
        if (!pass || (pass.length() < passwordMinLength)) {
            return [ok: false, message: "password must be minimum $passwordMinLength character long"]
        }

        if (passConfirm != pass) {
            return [ok: false, message: "The passwords you entered do not match"]
        }

        if (passwordMustContainLowercaseLetter && !(pass =~ /^.*[a-z].*$/)) {
            return [ok: false, message: "Password must contain lower case letters"]
        }

        if (passwordMustContainUpperaseLetter && !(pass =~ /^.*[A-Z].*$/)) {
            return [ok: false, message: "Password must contain uppercase letters"]
        }

        if (passwordMustContainNumbers && !(pass =~ /^.*[0-9].*$/)) {
            return [ok: false, message: "Password must contain numbers"]
        }

        if (passwordMustContainSymbols && !(pass =~ /^.*\W.*$/)) {
            return [ok: false, message: "Password must contain at least one symbol"]
        }

        if (passwordHistoryEnabled && passwordExistInHistory(user, pass)) {
            return [ok: false, message: "Password must be different then the last $passwordHistoryLength passwords"]
        }

        return [ok: true]
    }

    /**
     * Check if the password exists in user's password history
     * @param user
     * @param password
     * @return
     */
    @CompileDynamic
    boolean passwordExistInHistory(User user, String password) {
        List<SecPasswordHistory> passwordHistoryList = SecPasswordHistory.findAllByUser(user)
        passwordHistoryList.any { passwordEncoder.isPasswordValid(it.password, password, null) }
    }

}

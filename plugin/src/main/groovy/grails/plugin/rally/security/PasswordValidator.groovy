package grails.plugin.rally.security

import grails.gorm.transactions.Transactional
import grails.plugin.rally.security.SecPasswordHistory
import grails.plugin.rally.security.User
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.encoding.PasswordEncoder

@CompileStatic
class PasswordValidator {

    @Value('${grails.plugin.rally.security.password.minLength:4}')
    Integer passwordMinLength

    @Value('${grails.plugin.rally.security.password.mustContainNumbers:false}')
    boolean passwordMustContainNumbers

    @Value('${grails.plugin.rally.security.password.mustContainSymbols:false}')
    boolean passwordMustContainSymbols

    @Value('${grails.plugin.rally.security.password.mustContainUpperaseLetter:false}')
    boolean passwordMustContainUpperaseLetter

    @Value('${grails.plugin.rally.security.password.password.mustContainLowercaseLetter:false}')
    boolean passwordMustContainLowercaseLetter

    @Value('${grails.plugin.rally.security.password.historyEnabled:false}')
    boolean passwordHistoryEnabled

    @Value('${grails.plugin.rally.security.password.historyLength:4}')
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
     */
    @CompileDynamic
    @Transactional(readOnly = true)
    boolean passwordExistInHistory(User user, String password) {
        List<SecPasswordHistory> passwordHistoryList = SecPasswordHistory.findAllByUser(user)
        passwordHistoryList.any { passwordEncoder.isPasswordValid(it.password, password, null) }
    }

}

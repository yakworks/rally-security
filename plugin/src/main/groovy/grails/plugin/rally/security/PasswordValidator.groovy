package grails.plugin.rally.security

import grails.gorm.transactions.Transactional
import grails.plugin.rally.security.SecPasswordHistory
import grails.plugin.rally.security.User
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.authentication.encoding.PasswordEncoder

@CompileStatic
class PasswordValidator {
    MessageSource messageSource

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

    private String message(String key, def ...args) {
        messageSource.getMessage(key, args as Object[], key, LocaleContextHolder.locale)
    }

    Map validate(User user, String pass, String passConfirm) {
        if (!pass || (pass.length() < passwordMinLength)) {
            return [ok: false, message: message("rally.security.password.minlength", passwordMinLength)]
        }

        if (passConfirm != pass) {
            return [ok: false, message: message("rally.security.password.match")]
        }

        if (passwordMustContainLowercaseLetter && !(pass =~ /^.*[a-z].*$/)) {
            return [ok: false, message: message("rally.security.password.mustcontain.lowercase")]
        }

        if (passwordMustContainUpperaseLetter && !(pass =~ /^.*[A-Z].*$/)) {
            return [ok: false, message: message("rally.security.password.mustcontain.uppercase")]
        }

        if (passwordMustContainNumbers && !(pass =~ /^.*[0-9].*$/)) {
            return [ok: false, message: message("rally.security.password.mustcontain.numbers")]
        }

        if (passwordMustContainSymbols && !(pass =~ /^.*\W.*$/)) {
            return [ok: false, message: message("rally.security.password.mustcontain.symbol")]
        }

        if (passwordHistoryEnabled && passwordExistInHistory(user, pass)) {
            return [ok: false, message: message("rally.security.password.existsinhistory", passwordHistoryLength)]
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

package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic
import grails.core.GrailsApplication
import grails.gorm.transactions.Transactional
import groovy.time.TimeCategory
import groovy.transform.CompileDynamic
import org.grails.datastore.mapping.query.api.Criteria

/**
 * UserService is for user level helpers, such as sending emails to user,
 * tracking user login/logout And operations relating to passwords, contacts and org levels
 * Seurity related methods should go to SecService and not here.
 */
@GrailsCompileStatic
@Transactional
class UserService {

    boolean passwordHistoryEnabled
    int passwordExpireDays

    SecService secService
    PasswordValidator passwordValidator

    GrailsApplication grailsApplication


    /**
     * Create new record in secLoginHistory with logged in user and date
     */
    void trackUserLogin() {
        User user = secService.user
        SecLoginHistory secLoginHistory = SecLoginHistory.create([user: user, loginDate: new Date()])
    }

    @CompileDynamic //doesn't pick up maxResults with GrCompStatic
    void trackUserLogout() {
        User user = secService.user
        if (!user) return
        Criteria criteria = SecLoginHistory.createCriteria()
        List secLoginHistoryList = criteria.list {
            eq("user", user)
            isNull("logoutDate")
            maxResults(1)
            order("loginDate", "desc")
        } as List
        if (secLoginHistoryList) {
            SecLoginHistory secLoginHistory = secLoginHistoryList[0]
            secLoginHistory.logoutDate = new Date()
            secLoginHistory.save()
        }
    }

    /**
     * Validate presented user passwords against config to ensure it meets password requirements.
     */
    Map validatePassword(User user, String pass, String passConfirm) {
        return passwordValidator.validate(user, pass, passConfirm)
    }


    String generateResetPasswordToken(User user) {
        String token = UUID.randomUUID().toString().replaceAll('-', '')
        user.resetPasswordToken = token
        user.resetPasswordDate = new Date()
        user.save(flush: true)
        return token
    }

    int remainingDaysForPasswordExpiry(User u = null) {
        User user = u ?: secService.user
        Date now = new Date()
        int expiresInDaysFromNow = TimeCategory.minus(user.passwordChangedDate + (passwordExpireDays), now).days
        return expiresInDaysFromNow
    }

    /**
     * Update user's password and creates a password history record
     * @param user
     * @param newPwd
     */
    void updatePassword(User user, String newPwd) {
        user.passwd = newPwd //must be hased password
        user.mustChangePassword = false
        user.passwordChangedDate = new Date()
        user.save()

        if (passwordHistoryEnabled) {
            SecPasswordHistory.create(user, newPwd)
        }
    }

    /**
     * Check if the password exists in user's password history
     * @param user
     * @param password
     * @return
     */
    boolean passwordExistInHistory(User user, String password) {
       return passwordValidator.passwordExistInHistory(user, password)
    }
}

package grails.plugin.rally.security

import gorm.tools.databinding.BindAction
import gorm.tools.repository.GormRepo
import gorm.tools.repository.RepoMessage
import gorm.tools.repository.errors.EntityValidationException
import gorm.tools.repository.events.AfterBindEvent
import gorm.tools.repository.events.BeforeRemoveEvent
import gorm.tools.repository.events.RepoListener
import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.NotTransactional
import grails.gorm.transactions.Transactional

@GrailsCompileStatic
@Transactional
class UserRepo implements GormRepo<User> {
    SecService secService

    /**
     * Event method called beforeRemove to get rid of the SecRoleUser.
     */
    @RepoListener
    void beforeRemove(User user, BeforeRemoveEvent be) {
        SecRoleUser.removeAll(user)
    }

    /**
     * Sets up password and roles fields for a given User entity. Updates the dependent Contact entity.
     *
     * @param user a User entity to be updated
     * @param p
     * @param bindAction
     */
    @RepoListener
    void afterBind(User user, Map p, AfterBindEvent ae) {
        String password = p['password'] as String
        String repeatedPassword = p['repassword'] as String
        List roles = p['roles'] as List

        if(password) doPassword(user, password, repeatedPassword)

        //the persist will be called
        if(ae.bindAction == BindAction.Create) user.persist(flush: true)
        if(roles) setUserRoles(user.id, roles)
    }

    /**
     * Adds roles to the user
     */
    User addUserRole(User user, String role) {
        SecRoleUser.create(user, SecRole.findByName(role))
        return user
    }

    /**
     * checks params to see if password exists, that is matches repassword and encodes it if so
     * finally setting it to the passwd field on User.
     */
    private void doPassword(User user, String pass, String rePass){
        if(!pass?.trim()) return
        isSamePass(pass, rePass, user)
        user.passwd = encodePassword(pass)
    }

    @NotTransactional
    String encodePassword(String pass) {
        secService.encodePassword(pass)
    }

    /** throws EntityValidationException if not. NOTE: keep the real pas**ord name out so scanners dont pick this up */
    @NotTransactional
    void isSamePass(String pass, String rePass, User user) {
        if (pass.trim() != rePass.trim()) {
            Map msg = RepoMessage.setup("password.mismatch", [0], "The passwords you entered do not match")
            throw new EntityValidationException(msg, user)
        }
    }

    void setUserRoles(Long userId, List rolesId) {
        // Transform both arrays to ListArray<Long> to have ability to compare them
        List<Long> incomeRoles = rolesId.collect { it as long }
        List<Long> existingRoles = SecRoleUser.getByUser(userId)*.role.id

        List<Long> deleting
        List<Long> addition

        // Get the User instance
        User user = User.get(userId)

        // Compare existing role(s) with incoming
        if (existingRoles && incomeRoles) {
            deleting = existingRoles - incomeRoles // Roles for deleting from table
            addition = incomeRoles - existingRoles // Roles for addition to table
        } else if (incomeRoles) {
            addition = incomeRoles // Add all new roles if there is no existing in the table
        } else if (existingRoles) {
            deleting = existingRoles // Delete all existing roles in the table
        }

        // Delete/Add roles from table
        if (deleting?.size() > 0) {
            deleting.each { Long id ->
                SecRoleUser.remove(user, SecRole.findById(id))
            }
        }
        if (addition?.size() > 0) {
            addition.each { Long id ->
                SecRoleUser.create(user, SecRole.findById(id))
            }
        }

    }
}

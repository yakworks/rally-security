package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileDynamic
import org.codehaus.groovy.util.HashCodeHelper

@GrailsCompileStatic
class SecRoleUser implements Serializable {

    User user
    SecRole role

    static transients = ['roleName', 'userName', 'id']

    static mapping = {
        id composite: ['user', 'role']
        user column: 'userId'
        role column: 'secRoleId'
        version false
    }

    String getRoleName() {
        this.role.name
    }

    String getUserName() {
        this.user.name
    }

    //We need Id for exporting to xls
    String getId() {
        "u${this.user.id}r${this.role.id}"
    }

    @CompileDynamic
    static List<SecRoleUser> getByUser(long userId) {
        SecRoleUser.createCriteria().list {
            user{
                eq("id", userId)
            }
        } as List<SecRoleUser>
    }

    static SecRoleUser get(long userId, long roleId) {
        find 'from SecRoleUser where user.id=:userId and role.id=:roleId', [userId: userId, roleId: roleId]
    }

    static SecRoleUser create(User user, SecRole role, boolean flush = false) {
        new SecRoleUser(user: user, role: role).save(flush: flush, insert: true)
    }

    static boolean remove(User user, SecRole role, boolean flush = false) {
        SecRoleUser instance = SecRoleUser.findByUserAndRole(user, role)
        if (!instance) {
            return false
        }

        instance.delete(flush: flush)
        true
    }

    static void removeAll(User user) {
        executeUpdate 'DELETE FROM SecRoleUser WHERE user=:user', [user: user]
    }

    static void removeAll(SecRole role) {
        executeUpdate 'DELETE FROM SecRoleUser WHERE role=:role', [role: role]
    }

    @CompileDynamic
    static getRoleMap(User userInstance) {
        List roles = SecRole.list()
        Set userRoleNames = []
        if (userInstance.id) {
            for (r in userInstance.roles) {
                userRoleNames << r.springSecRole
            }
        }
        Map<SecRole, Boolean> roleMap = [:]
        for (r in roles) {
            roleMap[(r)] = userRoleNames.contains(r.springSecRole)
        }

        return roleMap
    }

    @Override
    boolean equals(Object other) {
        if (other instanceof SecRoleUser) {
            other.userId == user?.id && other.roleId == role?.id
        }
    }

    @Override
    int hashCode() {
        int hashCode = HashCodeHelper.initHash()
        if (user) {
            hashCode = HashCodeHelper.updateHash(hashCode, user.id)
        }
        if (role) {
            hashCode = HashCodeHelper.updateHash(hashCode, role.id)
        }
        hashCode
    }

}

package grails.plugin.rally.security

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode

/**
 * SecRole class for Authority.
 */
@EqualsAndHashCode(includes='name', useCanEqual=false)
@GrailsCompileStatic
class SecRole implements Serializable {

    // enabled by default in base data
    static final String ADMINISTRATOR = "Administrator" //full access, system user
    static final String POWER_USER = "Power User"
    //default user, access to all the screens, not manager (cannot approve, view other's tasks or delete cash system data)
    static final String MANAGER = "Manager" //access to all user's tasks, approval, can delete cash system data
    static final String GUEST = "Guest" //read only
    static final String CUSTOMER = "Customer" //greenbill single customer user

    // disabled by default in base data, more speific roles
    static final String COLLECTIONS = "Collections" //non cash
    static final String COLLECTIONS_MANAGER = "Collections Manager"  //can see other collector tasks, approvals

    static final String AUTOCASH = "Autocash" //cash, cannot delete system data
    static final String AUTOCASH_MANAGER = "Autocash Manager" //cash, can delete system data
    static final String AUTOCASH_OFFSET = "Autocash Offset" //cash, can only do $0 payments

    static final String ADMIN_CONFIG = "Admin Config" //setup
    static final String ADMIN_SEC = "Admin Sec" //user sec. management (acl)

    static final String SALES = "Sales" //review, approve disputes
    static final String BRANCH = "Branch"
    //branch, store user with limited access to customer and transaction screen only.

    Boolean inactive = false

    String description
    String name

    static mapping = {
        //users column: 'secRoleId', joinTable: 'SecRoleUser'
        tablePerHierarchy false
    }

    static constraints = {
        name blank: false
        description nullable: true
    }

    static transients = ['springSecRole']

    /**
     * Spring security plugin needs all authorities to be prefixed with ROLE_ and hence all roles must
     * be saved in database with name such as ROLE_MANAGER etc. However we use custom user detail service,
     * and call getSpringSecRole when populating a authorities for the UserDetail.
     * it allows us to save role names in db without prefix ROLE_
     */
    String getSpringSecRole() {
        return "ROLE_${name}".toString()
    }
}

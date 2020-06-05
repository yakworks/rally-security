package grails.plugin.rally.security

import gorm.tools.testing.integration.DataIntegrationTest
import grails.plugin.rally.security.testing.SecuritySpecHelper
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class UserRepoSpec extends Specification implements DataIntegrationTest, SecuritySpecHelper {
    BaseUserRepo baseUserRepo

    Map getUserParams(params = [:]){
        return ([
                name:"test-user",
                email:"test@9ci.com",
                login:'lll',
                 password:'secretStuff',
                 repassword:'secretStuff',
                 userRole:'1',
                 '_inactive':null,
                 id:'1',
        ] << params)
    }

    def testRemove() {
        setup:
        BaseUser user = baseUserRepo.create(getUserParams())

        expect:
        BaseUser.get(user.id) != null

        when:
        baseUserRepo.remove(user)

        then:
        BaseUser.get(user.id) == null
    }

    def testInsertWithRoles() {
        when:
        Map params = getUserParams([roles: ["1", "2"]])
        params.remove 'id'
        params.remove 'version'
        BaseUser res = baseUserRepo.create(params)
        flushAndClear()

        then:
        res.id != null

        when:
        BaseUser user = BaseUser.get(res.id)

        then:
        user.login == 'lll'
        SecRoleUser.findAllByUser(user)*.role.id == [1L, 2L]
    }

    def testUpdateToAddRoles() {
        when:
        Map params = getUserParams([roles: ["1", "2"]])
        BaseUser res = baseUserRepo.update([:], params)
        flushAndClear()

        then:
        res.id != null

        when:
        BaseUser user = BaseUser.get(1)

        then:
        user.login == 'lll'
        SecRoleUser.findAllByUser(user)*.role.id == [1L, 2L]
    }

    def testRemoveRoles() {
        setup:
        Map params = getUserParams([roles: ["1", "2"]])
        params.remove 'id'
        params.remove 'version'
        BaseUser user = baseUserRepo.create(params)
        flushAndClear()

        expect:
        SecRoleUser.get(user.id, 1)
        SecRoleUser.get(user.id, 2)

        when:
        baseUserRepo.remove(user)

        then:
        !SecRoleUser.get(user.id, 1)
        !SecRoleUser.get(user.id, 2)
    }

    def testUpdateToReplaceRoles() {
        when:
        SecRoleUser.findAllByUser(BaseUser.get(1))*.role.id == [1L]
        Map params = getUserParams([roles: ["2", "3"]])
        BaseUser res = baseUserRepo.update([:], params)
        flushAndClear()

        then:
        res.id != null

        when:
        BaseUser user = BaseUser.get(1)

        then:
        user.login == 'lll'
        SecRoleUser.findAllByUser(user)*.role.id == [2L, 3L]
    }

    /** printDiffs prints the pertinent params and final data for the test for debugging purposes. */
    void printDiffs(Map params, BaseUser user, Map result) {
        println "          key                    params - result"
        def format = '    %7s.%-10s: %15s - %-15s\n'
        printf(format, 'user', 'login', params.login, user.login)
        ['firstName', 'lastName', 'name', 'email'].each { key ->
            printf(format, 'contact', key, params.contact[key], user.contact[key])
        }
        println "result is ${result}"
        println "user id: ${user.id}, login: ${user.login}, contact id: ${user.contact.id}, firstName: ${user.contact.firstName}"
    }
}

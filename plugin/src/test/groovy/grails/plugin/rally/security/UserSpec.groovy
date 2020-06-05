package grails.plugin.rally.security

import gorm.tools.repository.errors.EntityValidationException
import gorm.tools.testing.TestDataJson
import gorm.tools.testing.hibernate.AutoHibernateSpec
import grails.plugin.rally.security.testing.SecuritySpecUnitTestHelper
import org.apache.commons.lang.RandomStringUtils

class UserSpec extends AutoHibernateSpec<BaseUser> implements SecuritySpecUnitTestHelper {

    List<Class> getDomainClasses() { [BaseUser, SecRole, SecRoleUser] }

    String genRandomEmail(){
        String ename = RandomStringUtils.randomAlphabetic(10)
        return "${ename}@baz.com"
    }

    @Override
    Map buildMap(Map args) {
        args.get('save', false)
        args.email = genRandomEmail()
        args.name = "test-user-${System.currentTimeMillis()}"
        args.login = "some_login_123"
        args.email = genRandomEmail()
        args
    }

    @Override
    BaseUser createEntity(Map args){
        entity = new BaseUser()
        args = buildMap(args)
        args << [password:'secretStuff', repassword:'secretStuff']
        entity = BaseUser.create(args)
        //We have to add 'passwd' field manually, because it has the "bindable: false" constraint
        entity.passwd = 'test_pass_123'
        entity.persist()

        get(entity.id)
    }

    @Override
    BaseUser persistEntity(Map args){
        args.get('save', false) //adds save:false if it doesn't exists
        args['passwd'] = "test"
        entity = build(buildMap(args))
        assert entity.persist(flush: true)
        return get(entity.id)
    }

    def "test update fail"() {
        when:
        BaseUser user = createEntity()
        Map params = [id: user.id, login: null]
        BaseUser.update(params)

        then:
        thrown EntityValidationException
    }

    def "insert with roles"() {
        setup:
        SecRole.create(TestDataJson.buildMap([save:false, name: 'ROLE_1'], SecRole))
        SecRole.create(TestDataJson.buildMap([save:false, name: 'ROLE_2'], SecRole))

        expect:
        SecRole.get(1) != null
        SecRole.get(1).name == "ROLE_1"

        SecRole.get(2) != null
        SecRole.get(2).name == "ROLE_2"

        when:
        Map data = buildMap([:])
        data.roles = ["1", "2"]
        data << [password:'secretStuff', repassword:'secretStuff']
        BaseUser user = BaseUser.create(data)
        flush()

        then:
        user != null
        SecRoleUser.count() == 2
        SecRoleUser.findAllByUser(user)*.role.id == [1L, 2L]
    }

    def "user name"() {
        when:
        Map data = buildMap([:])
        data << [password:'secretStuff', repassword:'secretStuff']
        BaseUser user = BaseUser.create(data)
        flush()

        then:
        user.name.startsWith "test"
    }

}

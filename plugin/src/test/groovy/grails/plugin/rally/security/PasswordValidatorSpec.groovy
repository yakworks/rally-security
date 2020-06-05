package grails.plugin.rally.security

import gorm.tools.testing.unit.DataRepoTest
import grails.plugin.rally.security.testing.SecuritySpecUnitTestHelper
import spock.lang.Specification

class PasswordValidatorSpec extends Specification implements  DataRepoTest, SecuritySpecUnitTestHelper {

    void setup() {
        mockDomain(BaseUser)
        new BaseUser(name: "admin", login:"admin", email:"admin@9ci.com", passwd: "test").save()
    }

    void test_validate() {
        setup:
        PasswordValidator validator = new PasswordValidator()
        validator.messageSource = messageSource

        Map result

        when: "password length"
        validator.passwordMinLength = 4
        result = validator.validate(BaseUser.get(1), "123", "123")

        then:
        result.ok == false
        result.message == "rally.security.password.minlength"

        when: "password match"
        validator.passwordMinLength = 3
        result = validator.validate(BaseUser.get(1), "123", "1234")

        then:
        result.ok == false
        result.message == "rally.security.password.match"

        when: "require lowercase"
        validator.passwordMinLength = 4
        validator.passwordMustContainLowercaseLetter = true
        result = validator.validate(BaseUser.get(1), "ABCD", "ABCD")

        then:
        result.ok == false
        result.message == "rally.security.password.mustcontain.lowercase"

        when: "require uppercase"
        validator.passwordMinLength = 4
        validator.passwordMustContainUpperaseLetter = true
        result = validator.validate(BaseUser.get(1), "abcd", "abcd")

        then:
        result.ok == false
        result.message == "rally.security.password.mustcontain.uppercase"

        when: "require numbers"
        validator.passwordMinLength = 4
        validator.passwordMustContainNumbers = true
        result = validator.validate(BaseUser.get(1), "abcD", "abcD")

        then:
        result.ok == false
        result.message == "rally.security.password.mustcontain.numbers"


        when: "require symbol"
        validator.passwordMinLength = 4
        validator.passwordMustContainSymbols = true
        result = validator.validate(BaseUser.get(1), "ab1D", "ab1D")

        then:
        result.ok == false
        result.message == "rally.security.password.mustcontain.symbol"

        when: "all good"
        validator.passwordMinLength = 4
        validator.passwordMustContainSymbols = true
        result = validator.validate(BaseUser.get(1), "ab1D#", "ab1D#")

        then:
        result.ok == true

        cleanup:
        validator.passwordMinLength = 4
        validator.passwordMustContainLowercaseLetter = false
        validator.passwordMustContainUpperaseLetter = false
        validator.passwordMustContainSymbols = false
        validator.passwordMustContainNumbers = false
    }
}

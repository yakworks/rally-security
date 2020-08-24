# rally-security
The plugin is based on spring-security core. It encapsulates and provides common security related functionality

### Some of the functionality provided
- Common configuration
- Domain classes for User, Role etc.
- Login history - Tracking user login and logout
- Password history & Password expiry
- Configurable password strength validation
- Helpers to check if user is authenticated, authenticate a user programmatically etc.


### Installation

```
compile "org.grails.plugins:rally-security:$version"
```

Configuration
----
The plugin comes with a default configuration. it configures `BaseUser` as User domain class, `SecRoleUser` as
 authorityJoinClass, MD5 as default algorithm for passwords. By default, all the URLs are secured and requires either
  authenticated or rememberd user to access any URL.
  
#### Configuring secured urls.  
By default, all urls are secured. Apps can configure different url patterns using `interceptUrlMap`

Example

```groovy
grails {
    plugin {
        springsecurity {
            active = true
            interceptUrlMap = [
               [pattern: '/static/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
               [pattern: '/assets/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],

            ]
      }
   }
}
```

#### Custom User domain class
Plugin provides `BaseUser` which is used as the User class. If application feels the need to override the User class
 it can be done by extending the BaseUser as shown below
 
 ```groovy
 class User extends BaseUser {
 
  static mapping = {
        tablePerHierarchy true
     }
    
}
```

```groovy

grails {
    plugin {
        springsecurity {
            userLookup.userDomainClassName = 'xx.xx.User'
      }
   }
}

```

### Password strength validation
Plugin registers a bean named passwordValidator which can be used for password validation

**Configuration**

| key                                                                       | default value | Notes                  |
|---------------------------------------------------------------------------|---------------|------------------------|
| grails.plugin.rally.security.password.minLength                           | 4             | Min length of password |
| grails.plugin.rally.security.password.mustContainNumbers:false            | false         |                        |
| grails.plugin.rally.security.password.mustContainSymbols                  | false         |                        |
| grails.plugin.rally.security.password.mustContainUpperaseLette            | false         |                        |
| grails.plugin.rally.security.password.password.mustContainLowercaseLetter | false         |                        |

### Password history
Plugin can be configured to maintain password history, and do prevent reuse of specified number of past passwords

| key                                                                       | default value | Notes                                 |
|---------------------------------------------------------------------------|---------------|---------------------------------------|
| grails.plugin.rally.security.password.historyEnabled                      | false         | if password history should be enabled |
| grails.plugin.rally.security.password.historyLength                       |               | Length of password history.           |

### Password expiry

| key                                                                       | default value | Notes                             |
|---------------------------------------------------------------------------|---------------|-----------------------------------|
| grails.plugin.rally.security.password.expireEnabled                       | false         | if password expiry enabled        |
| grails.plugin.rally.security.password.expireDays                          | 30            | Days after which passwords expire |

If password expiry is enabled, once the password has expired, plugin sets a flag `warnAboutPasswordExpiry` on session
 from `RallyLoginHandler`. The flag can be used by application to force user to change password, or just warn user.
 
### Login history
Plugin maintains login history - The associated domains are `SecLoginHistory` which can be used by app to provide UI.
 


### Services & Helpers
- Plugin provides SecService & UserService which contains common helpers. Check the source for more details.


### Testing support

**SecuritySpecUnitTestHelper**

Provides support for unit tests which needs a logged in user. It registered required beans and provides method to
 authenticate a user.
 
 ```groovy
class UserSpec implements SecuritySpecUnitTestHelper {}
   final private static String  ROLE_USER = "User" 
   final private static String  ROLE_CUSTOMER = "Customer"

   void testSomething() {
      setup:
         User user = new User(login:"xx")
         authenticate(user, ROLE_USER, ROLE_CUSTOMER)
       when:
          test some thing
   }



}

```

**SecuritySpecHelper**

Provides testing support for Integration tests. and provides the `authenticate` method with same signature as above.  

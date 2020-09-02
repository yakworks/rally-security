grails {
    plugin {
        springsecurity {
            active = true
            interceptUrlMap = [
                    [pattern: '/**', access: ['IS_AUTHENTICATED_REMEMBERED']]
            ]
            //MAPPING and AUTH
            userLookup.authoritiesPropertyName = 'roles'
            userLookup.authorityJoinClassName = 'grails.plugin.rally.security.SecRoleUser'
            userLookup.enabledPropertyName = 'enabled'
            userLookup.passwordPropertyName = 'passwd'
            userLookup.userDomainClassName = 'grails.plugin.rally.security.BaseUser'
            userLookup.usernamePropertyName = 'login'
            userLookup.accountExpiredPropertyName = null
            userLookup.accountLockedPropertyName = null
            userLookup.passwordExpiredPropertyName = null

            authority.nameField = 'springSecRole'
            password.algorithm = 'MD5'
            securityConfigType = "InterceptUrlMap"
            adh.errorPage = null //null out so it can be custom
            logout.handlerNames = ['rememberMeServices', 'rallyLogoutHandler']

            //events
            useSecurityEventListener = true
            onInteractiveAuthenticationSuccessEvent = { e, appCtx ->
                // handle AuthenticationSuccessEvent
                def userService = appCtx.getBean('userService')
                userService.trackUserLogin()
            }
            rest {
                token.storage.jwt.secret = 'sWXY3VMm4wKAGoRZg8r3ftZcjrKvmExghY'
            }
        }
    }
}

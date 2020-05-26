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
            userLookup.userDomainClassName = 'grails.plugin.rally.security.User'
            userLookup.usernamePropertyName = 'login'
            userLookup.accountExpiredPropertyName = null
            userLookup.accountLockedPropertyName = null
            userLookup.passwordExpiredPropertyName = null

            authority.nameField = 'springSecRole'
            password.algorithm = 'MD5'
            securityConfigType = "InterceptUrlMap"
            adh.errorPage = null //null out so it can be custom
            logout.handlerNames = ['rememberMeServices', 'nineLogoutHandler']

            //events
            useSecurityEventListener = true
            onInteractiveAuthenticationSuccessEvent = { e, appCtx ->
                // handle AuthenticationSuccessEvent
                def userService = appCtx.getBean('userService')
                userService.trackUserLogin()
            }
        }
    }
}
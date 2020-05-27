grails {
    plugin {
        springsecurity {
            active = true
            autoLoginEnabled = false
            interceptUrlMap = [
                    [pattern: '/static/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/assets/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/j_spring_security_check', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/login/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/logout/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/images/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/css/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/js/**', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/**/*.js', access: ['IS_AUTHENTICATED_ANONYMOUSLY']],
                    [pattern: '/', access: ['IS_AUTHENTICATED_REMEMBERED']],
                    [pattern: '/**', access: ['IS_AUTHENTICATED_REMEMBERED']]
            ]
        }
    }
}

grails.plugin.springsecurity.filterChain.chainMap = [
        //unsecured
        [pattern: '/assets/**',      filters: 'none'],
        [pattern: '/**/js/**',       filters: 'none'],
        [pattern: '/**/css/**',      filters: 'none'],
        [pattern: '/**/images/**',   filters: 'none'],
        [pattern: '/**/favicon.ico', filters: 'none'],
        [pattern: '/j_spring_security_check', filters: 'none'],

        //Stateless chain for REST API
        [
                pattern: '/api/**',
                filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
        ],

        //Traditional chain for form based login
        [
                pattern: '/**',
                filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
        ]
]

grails.plugin.springsecurity.rest.token.validation.useBearerToken = false
grails.plugin.springsecurity.rest.token.validation.headerName = 'X-Auth-Token'

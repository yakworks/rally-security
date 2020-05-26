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

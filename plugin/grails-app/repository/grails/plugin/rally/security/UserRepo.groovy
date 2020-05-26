package grails.plugin.rally.security;

import gorm.tools.repository.GormRepo;
import grails.compiler.GrailsCompileStatic;
import grails.gorm.transactions.Transactional;

@GrailsCompileStatic
@Transactional
class UserRepo implements GormRepo<User> {
    SecService secService

    String encodePassword(String pass) {
        secService.encodePassword(pass)
    }
}
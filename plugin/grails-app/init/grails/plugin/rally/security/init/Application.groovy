/*
* Copyright 2020 Yak.Works - Licensed under the Apache License, Version 2.0 (the "License")
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*/
package grails.plugin.rally.security.init

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.compiler.GrailsCompileStatic
import grails.plugins.metadata.PluginSource

@PluginSource
@GrailsCompileStatic
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}

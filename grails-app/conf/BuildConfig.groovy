grails.project.work.dir = 'target'

// added to make shiro work under 2.4.2
grails.project.dependency.resolver = "maven" 

grails.project.dependency.resolution = {
    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
		
        /*runtime ('org.apache.shiro:shiro-core:1.2.1',
			'org.apache.shiro:shiro-web:1.2.1',
			'org.apache.shiro:shiro-spring:1.2.1',
			'org.apache.shiro:shiro-ehcache:1.2.1',
			'org.apache.shiro:shiro-quartz:1.2.1') {
			excludes 'ejb', 'jsf-api', 'servlet-api', 'jsp-api', 'jstl', 'jms',
			'connector-api', 'ehcache-core', 'slf4j-api', 'commons-logging'
			}
		*/	
    }

    plugins {
       // runtime ':cors:1.1.6'
       	build ':release:3.0.1', ':rest-client-builder:2.0.3', {
            export = false
        }

		runtime ':shiro:1.2.1', {
				excludes 'quartz','hibernate'
		}
    }
}

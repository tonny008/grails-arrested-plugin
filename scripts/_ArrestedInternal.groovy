import grails.util.Holders
import grails.util.Metadata
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.springframework.core.io.ResourceLoader
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsCompile")

overwriteAll = false

def appVersion=Metadata.current.'app.grails.version'
def appName=Metadata.current.'app.name'
def shortname= appName.toString().replaceAll(/(\_|\-|\.)/, '')

installTemplate = { String artefactName, String artefactPath, String templatePath ->
    installTemplateEx(artefactName, artefactPath, templatePath, artefactName, null)
}

installTemplateEx = { String artefactName, String artefactPath, String templatePath, String templateName, Closure c ->
    // Copy over the standard auth controller.
    def artefactFile = "${basedir}/${artefactPath}/${artefactName}"
	if (!okToWrite(artefactFile)) {
		return
	}

    // Copy the template file to the 'grails-app/controllers' directory.
    templateFile = "${arrestedPluginDir}/src/templates/${templatePath}/${templateName}"
    if (!new File(templateFile).exists()) {
        ant.echo("[Arrested plugin] Error: src/templates/${templatePath}/${templateName} does not exist!")
        return
    }
    ant.copy(file: templateFile, tofile: artefactFile, overwrite: true)

    // Perform any custom processing that may be required.
    if (c) {
        c.delegate = [artefactFile: artefactFile]
        c.call()
    }
    event("CreatedFile", [artefactFile])
}

installTemplateView = { domainClass, String artefactName, String artefactPath, String templatePath, String templateName, Closure c ->
    // Copy over the standard auth controller.
    Template renderEditorTemplate
    SimpleTemplateEngine engine = new SimpleTemplateEngine()
    GrailsPluginManager pluginManager
    ResourceLoader resourceLoader
    def cp

    def artefactFile = "${basedir}/${artefactPath}/${artefactName}"
	if (!okToWrite(artefactFile)) {
		return
	}

    // Copy the template file to the 'grails-app/controllers' directory.
    templateFile = "${arrestedPluginDir}/src/templates/${templatePath}/${templateName}"
    if (!new File(templateFile).exists()) {
        ant.echo("[Arrested plugin] Error: src/templates/${templatePath}/${templateName} does not exist!")
        return
    }

    renderEditorTemplate = engine.createTemplate(new File(templateFile))
    String plugin = Holders.pluginManager.getGrailsPlugin("hibernate")
	if (!plugin) {
		plugin = Holders.pluginManager.getGrailsPlugin("hibernate4")
	}
	
    if (plugin) {
	    cp = domainClass.constrainedProperties
	}
    def binding = [
            className: domainClass.getShortName(),
            domainTitle: domainClass.getPropertyName(),
            domainInstance: domainClass.getPropertyName(),
            domainClass: domainClass.getProperties(),
            pluginManager: pluginManager,
            cp: cp]

    ant.copy(file: templateFile, tofile: artefactFile, overwrite: true)

    File file = new File(artefactFile)
    BufferedWriter output = new BufferedWriter(new FileWriter(file))
    output.write(renderEditorTemplate.make(binding).toString())
    output.close()
    // Perform any custom processing that may be required.
    if (c) {
        c.delegate = [artefactFile: artefactFile]
        c.call()
    }
    event("CreatedFile", [artefactFile])
}

target(createViewController: "Creates view") {
    depends(compile)
    depends(loadApp)
    def (pkg, prefix) = parsePrefix()
    def domainClasses = grailsApp.domainClasses
    domainClasses.each {
        domainClass ->
            if (domainClass.getShortName() == prefix) {
                def className = domainClass.getPropertyName()
                installTemplateView(domainClass, "list.gsp", "grails-app/views/${packageToPath(pkg)}${className}", "views/view", "list.html") {}
                installTemplateView(domainClass, "edit.gsp", "grails-app/views/${packageToPath(pkg)}${className}", "views/view", "edit.html") {}
				
            }
    }
    depends(compile)
}

target(createToken: "Create a token class") {
    depends(compile)
    def (pkg, prefix) = parsePrefix1()
    installTemplateEx("ArrestedToken.groovy", "grails-app/domain${packageToPath(pkg)}", "classes", "ArrestedToken.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
}

target(createUser: "Create a user class") {
    depends(compile)
    def (pkg, prefix) = parsePrefix1()	
    installTemplateEx("ArrestedUser.groovy", "grails-app/domain${packageToPath(pkg)}", "classes", "ArrestedUser.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
}

target(createRole: "Create a role class") {
	
	depends(compile)
	def (pkg, prefix) = parsePrefix1()	
	installTemplateEx("ArrestedRole.groovy", "grails-app/domain${packageToPath(pkg)}", "classes", "ArrestedRole.groovy") {
		ant.replace(file: artefactFile) {
			ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
		}
	}
	depends(compile)
}

target(createRealm: "Create a DB Realm class") {
	
	depends(compile)
	def (pkg, prefix) = parsePrefix1()
	installTemplateEx("DbRealm.groovy", "grails-app/realms${packageToPath(pkg)}", "realms/arrested", "DbRealm.groovy") {
		ant.replace(file: artefactFile) {
			ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
		}
	}
	depends(compile)
}

target(createUserController: "Create a user class") {
    depends(compile)
    def (pkg, prefix) = parsePrefix1()
    installTemplateEx("ArrestedUserController.groovy", "grails-app/controllers${packageToPath(pkg)}", "controllers", "ArrestedUserController.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
    installTemplateEx("ArrestedUserControllerIntegrationTest.groovy", "test/integration${packageToPath(pkg)}", "test/integration", "ArrestedUserControllerIntegrationTest.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
    installTemplateEx("ArrestedUserControllerUnitTest.groovy", "test/unit${packageToPath(pkg)}", "test/unit", "ArrestedUserControllerUnitTest.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)

    println("ArrestedUserController.groovy created and Unit & Integration Tests")
}

target(createArrestedController: "Create the controller Arrested") {
    depends(compile)
    def (pkg, prefix) = parsePrefix1()
    installTemplateEx("ArrestedController.groovy", "grails-app/controllers/arrested", "controllers", "ArrestedController.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
    println("ArrestedController.groovy created")
}

target(createAuth: "Create a authentication controller") {
    depends(compile)
    def (pkg, prefix) = parsePrefix1()
    installTemplateEx("AuthController.groovy", "grails-app/controllers${packageToPath(pkg)}", "controllers", "AuthController.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
    installTemplateEx("AuthControllerIntegrationTests.groovy", "test/integration${packageToPath(pkg)}", "test/integration", "AuthControllerIntegrationTests.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
    installTemplateEx("AuthControllerUnitTest.groovy", "test/unit${packageToPath(pkg)}", "test/unit", "AuthControllerUnitTest.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
}

target(createFilter: "Create a security filter") {
    depends(compile)
    def (pkg, prefix) = parsePrefix1()
    installTemplateEx("SecurityFilters.groovy", "grails-app/conf${packageToPath(pkg)}", "configuration", "SecurityFilters.groovy") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
        }
    }
    depends(compile)
}

target(createBean: "Create spring/resources.groovy") {
	depends(compile)
	def (pkg, prefix) = parsePrefix1()
	def file="spring/resources.groovy"
	def resourcesFile = "${basedir}/grails-app/conf/${file}"
	if (!okToWrite(resourcesFile)) {
		return
	}
	templateFile = "${arrestedPluginDir}/src/templates/configuration/${file}"
	if (!new File(templateFile).exists()) {
		ant.echo("[Arrested plugin] Error: src/templates/configuration/${file} does not exist!")
		return
	}
	ant.copy(file: templateFile, tofile: resourcesFile, overwrite: true)
	depends(compile)
}


target(updateUrl: "Updating the Url Mappings") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
    def configFile = new File("${basedir}/grails-app/conf/UrlMappings.groovy")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "class UrlMappings {"
        writer.writeLine "    static mappings = {"
        writer.writeLine "        \"/\"(view:\"/index\")"
        writer.writeLine "        \"/\$controller/\$action?\"(parseRequest: true)"
        writer.writeLine "    }"
        writer.writeLine "}"
    }
    println("UrlMappings.groovy updated")
    depends(compile)
}

target(updateResources: "Update the application resources") {
    depends(compile)
    depends(loadApp)
	def appStyle=validateStyling(appVersion)
	if (appStyle.is('resources')) {
	    def (pkg, prefix) = parsePrefix()
	    def domainClasses = grailsApp.domainClasses
	    def names = []
		def engine = new SimpleTemplateEngine()
	    domainClasses.each {
	        domainClass ->
	            if (domainClass.getShortName() != "ArrestedUser" && domainClass.getShortName() != "ArrestedToken") {
	                names.add([propertyName: domainClass.getPropertyName(), className: domainClass.getShortName()])
	            }
	    }
		StringBuilder rul=new StringBuilder()
		def cpathUser=verifyGrailsVersion(appVersion, 'js', 'custom-'+appName)
		names.each {
			if (new File("${basedir}/${cpathUser}/${it.className}Ctrl.js").exists()) {
				rul.append('\t\tresource url: \'js/').append(it.className).append('Ctrl.js\'\n')
			}
		}
		def rulConf = [customAngularControllers: rul]
		def ruleCont = createTemplate(engine, 'configuration/ApplicationResources.groovy', rulConf)
		writeToFile("grails-app/conf/ApplicationResources.groovy",ruleCont.toString())
		   depends(compile)
	    println("ApplicationResources.groovy updated")
	} 
}


target(createAngularService: "Create the angular service") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
	def cpathIndex=verifyGrailsVersion(appVersion, 'js', appName)
    //installTemplateEx("services.js", "web-app/js/${packageToPath(pkg)}", "views/controllers", "services.js") {}
	installTemplateEx("services.js", "${cpathIndex}/${packageToPath(pkg)}", "views/controllers", "services.js") {}
    depends(compile)
}

target(createAngularIndex: "Create the angular file configuration") {
    depends(compile)
    depends(loadApp)
    def (pkg, prefix) = parsePrefix()
    def domainClasses = grailsApp.domainClasses
    def names = []
	def engine = new SimpleTemplateEngine()
    domainClasses.each {
        domainClass ->
            if (domainClass.getShortName() != "ArrestedUser" && domainClass.getShortName() != "ArrestedToken") {
                names.add([propertyName: domainClass.getPropertyName(), className: domainClass.getShortName()])
            }
    }
	
	def cpathIndex=verifyGrailsVersion(appVersion, 'js', appName)
    //def configFile = new File("${basedir}/web-app/js/index.js")
	localmkdir("$basedir/${cpathIndex}")
	def myFile="${basedir}/${cpathIndex}/index.js"
	if (!okToWrite(myFile)) {
		return
	}
	def configFile = new File(myFile)
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
	
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "'use strict';"
        writer.writeLine "var " + shortname + " = angular.module('" + Metadata.current.'app.name' + "', ['services','ngRoute']);"
		
		writer.writeLine(shortname + ".config(function (\$datepickerProvider) {")
		writer.writeLine("    angular.extend(\$datepickerProvider.defaults, {")
		writer.writeLine("        dateFormat: 'dd-MM-yyyy',")
		writer.writeLine("        modelDateFormat: \"yyyy-MM-ddTHH:mm:ss\"")
		writer.writeLine("    })});")
		
		//writer.writeLine "var " + shortname + " = angular.module('" + Metadata.current.'app.name' + "', ['services','ngRoute']);"
        writer.writeLine shortname + ".config([\n" +
                "    '\$routeProvider',\n" +
                "    function(\$routeProvider) {\n" +
                "        \$routeProvider."
		//writer.writeLine "            when('/dashboard', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/dashboard', controller: 'UserCtrl', resolve: { myLang: function(LangService) {return LangService.getLang()}}})."
		writer.writeLine "            when('/dashboard', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/dashboard', controller: 'UserCtrl'})."
        writer.writeLine "            when('/login', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showLogin', controller: 'UserCtrl'})."
		writer.writeLine "            when('/signup', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showSignup', controller: 'UserCtrl'})."
		writer.writeLine "            when('/updatepassword', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showUpdatePassword', controller: 'UserCtrl'})."
		writer.writeLine "            when('/updateusername', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showUpdateUsername', controller: 'UserCtrl'})."
		writer.writeLine "            when('/confirmupdate', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showUpdated', controller: 'UserCtrl'})."
		def cpathUser=verifyGrailsVersion(appVersion, 'js', 'custom-'+appName)
		names.each {
			if (new File("${basedir}/${cpathUser}/${it.className}Ctrl.js").exists()) {
				writer.writeLine "            when('/" + it.propertyName + "/create', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/edit', controller: '" + it.className + "Ctrl'})."
				writer.writeLine "            when('/" + it.propertyName + "/edit', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/edit', controller: '" + it.className + "Ctrl'})."
				writer.writeLine "            when('/" + it.propertyName + "/list', {templateUrl: '/" + Metadata.current.'app.name' + "/"  + it.propertyName + "/listing', controller: '" + it.className + "Ctrl'})."
				writer.writeLine "            when('/" + it.propertyName + "', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/listing', controller: '" + it.className + "Ctrl'})."
			}	
        }
        writer.writeLine "            otherwise({redirectTo: '/login'});"
        writer.writeLine "    }"
        writer.writeLine "]);"
		
		
		/*.when('/dashboard', {
			templateUrl: 'views/dashboard.html',
			controller: 'DashboardController',
			resolve: {
			user: function(SessionService) {
			return SessionService.getCurrentUser();
			}
			}
			})*/
		
	 }
	
	 def addConf = [shortname: shortname]
	 def cpathDirective=verifyGrailsVersion(appVersion, 'js', appName)
	 def direc = createTemplate(engine, 'views/controllers/arrestedDirectives.js', addConf)
	 writeToFile(cpathDirective+'/arrestedDirectives.js',direc.toString())
	
	 def arrestedServices = createTemplate(engine, 'views/controllers/arrestedServices.js', addConf)
	 writeToFile(cpathDirective+'/arrestedServices.js',arrestedServices.toString())
	 
    depends(compile)
    println("index.js arrestedDirectives.js arrestedServices.js created")
}

target(createController: "Creates a standard controller") {
    depends(compile)
    depends(loadApp)
    def (pkg, prefix) = parsePrefix()
    def className = prefix + "Controller"
    def domainClasses = grailsApp.domainClasses
    domainClasses.each {
        domainClass ->
            if (domainClass.getShortName() == prefix) {
                pkg = domainClass.getPackageName()
                installTemplateView(domainClass, "${className}.groovy", "grails-app/controllers${packageToPath(pkg)}", "controllers", "Controller.groovy") {
                    ant.replace(file: artefactFile) {
                        ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
                        ant.replacefilter(token: '@controller.name@', value: className)
                        ant.replacefilter(token: '@class.name@', value: prefix)
                        ant.replacefilter(token: '@class.instance@', value: prefix)
                    }
                }
                installTemplateEx("${className}IntegrationTest.groovy", "test/integration${packageToPath(pkg)}", "test/integration", "integrationTest.groovy") {
                    ant.replace(file: artefactFile) {
                        ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
                        ant.replacefilter(token: "@controller.name@", value: className)
                        ant.replacefilter(token:"@class.name@",value:prefix)
                        ant.replacefilter(token: '@class.instance@', value: prefix)
                    }
                }
                depends(compile)
                installTemplateEx("${className}UnitTest.groovy", "test/unit${packageToPath(pkg)}", "test/unit", "unitTest.groovy") {
                    ant.replace(file: artefactFile) {
                        ant.replacefilter(token: "@package.line@", value: (pkg ? "package ${pkg}\n\n" : ""))
                        ant.replacefilter(token: "@controller.name@", value: className)
                        ant.replacefilter(token:"@class.name@",value:prefix)
                        ant.replacefilter(token: '@class.instance@', value: prefix)
                    }
                }
                depends(compile)
            }
    }
    depends(compile)
}

target(createJSController: "Creates a standard angular controller") {
    depends(compile,loadApp)
	def engine = new SimpleTemplateEngine()
    def (pkg, prefix) = parsePrefix1()
    def className = prefix + "Ctrl"
	def cpathController=verifyGrailsVersion(appVersion, 'js', 'custom-'+appName)
    def domainClasses = grailsApp.domainClasses
	def constraintsList = "";
    domainClasses.each {
        domainClass ->
            if (domainClass.getShortName() == prefix) {	
				def sb = new StringBuilder("")
				domainClass.constrainedProperties?.each {key, value ->
					if(domainClass.constrainedProperties[key].inList) {
						sb.append("\$rootScope." + key + "s").append(
							 " = [\"" + domainClass.constrainedProperties[key].inList.join("\",\"") + "\"];\n")
					}
				}
				constraintsList = sb;
				def addConf=[contName:className,className:prefix,
					instance:domainClass.getPropertyName(),appName:appName,
					constraintList:constraintsList]
				
				def clsjs = createTemplate(engine, 'views/controllers/Controller.js', addConf)
				writeToFile("${cpathController}/${className}.js",clsjs.toString())
            }
    }
    depends(compile)
}

target(createAngularUser: "Create the angular user controller") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
	def engine = new SimpleTemplateEngine()
	def cpathUser=verifyGrailsVersion(appVersion, 'js', 'custom-'+appName)
	def addConf=[appName:appName]
	localmkdir("$basedir/${cpathUser}")
	def usrctl = createTemplate(engine, 'views/controllers/userController.js', addConf)
	writeToFile("${cpathUser}/userCtrl.js",usrctl.toString())

	def dashctl = createTemplate(engine, 'views/controllers/dashboardController.js', addConf)
        writeToFile("${cpathUser}/dashboardCtrl.js",dashctl.toString())


	
	installTemplateEx("clockCtrl.js", cpathUser, "views/controllers", "clockController.js") {}
    installTemplateEx("login.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "login.html") {}
	installTemplateEx("dashboard.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "dashboard.html") {}
	installTemplateEx("signup.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "signup.html") {}
	installTemplateEx("update.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "update.html") {}
	installTemplateEx("update-username.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "update-username.html") {}
    println("userController.js and login.html signup.html clockCtrl.js created")
    depends(compile)
}

target(updateLayout: "Update the layout view") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
	def engine = new SimpleTemplateEngine()
	def addConf = [app: shortname]
	def appStyle=validateStyling(appVersion)
	
	def jpathController=verifyGrailsVersion(appVersion, 'js', '')
	File dir = new File(arrestedPluginDir, "src/templates/assets/javascripts")
	dir.eachFileRecurse{
		f -> if (f.isDirectory()) {
			if (!okToWrite("${basedir}/${jpathController}/$f.name")) {
				return
			}
			println "Creating ${appStyle} javascripts folder: $f.name"
			dir = new File(arrestedPluginDir, "src/templates/assets/javascripts/$f.name")
			copy(todir: new File(basedir, jpathController+'/'+f.name)) {
				fileset dir: dir
			}
		}
	}
	
	String ngtblLoc='src/templates/views/web-app/ng-table'
	def ngTblView="web-app/ng-table"
	dir = new File(arrestedPluginDir, ngtblLoc)
	copy(todir: new File(basedir, ngTblView)) {
		fileset dir: dir
	}
	dir.eachFileRecurse{
		f -> if (f.isDirectory()) {
			if (!okToWrite("${basedir}/${ngTblView}/$f.name")) {
				return
			}
			println "Creating ${appStyle} ng-table folder: $f.name"
			dir = new File(arrestedPluginDir, "${ngtblLoc}/$f.name")
			copy(todir: new File(basedir, ngTblView+'/'+f.name)) {
				fileset dir: dir
			}
		}
		
	}
	


			
	def navbar = createTemplate(engine, 'views/layouts/_navbar.gsp', addConf)
	writeToFile("grails-app/views/layouts/_navbar.gsp",navbar.toString())
	
	def csspathController=verifyGrailsVersion(appVersion, 'css', '')
	
	dir = new File(arrestedPluginDir, "src/templates/assets/stylesheets")
	if (!okToWrite(csspathController)) {
		return
	}
	println "Creating fonts folder: ${csspathController}"
	copy(todir: new File(basedir, csspathController)) {
		fileset dir: dir
	}
	
	def i18n="${basedir}/grails-app/i18n"
	dir = new File(arrestedPluginDir, "src/templates/i18n")
	if (!okToWrite(i18n)) {
		return
	}
	println "Creating i18n folder: ${i18n} and copying DE and EN"
	copy(todir: new File(basedir, "grails-app/i18n")) {
		fileset dir: dir
	}
	
	
	if (appStyle.is('assets')) {
	
		dir = new File(arrestedPluginDir, "src/templates/fonts")
		if (!okToWrite("${basedir}/web-app/fonts")) {
			return
		}
		println "Creating fonts folder: web-app/fonts"
		copy(todir: new File(basedir, 'web-app/fonts')) {
			fileset dir: dir
		}
		
		def maingsp = createTemplate(engine, 'assets/views/layouts/main.gsp', addConf)
		writeToFile("grails-app/views/layouts/main.gsp",maingsp.toString())
		
		def indexgsp = createTemplate(engine, 'assets/views/index.gsp', addConf)
		writeToFile("grails-app/views/index.gsp",indexgsp.toString())
		
		def appjs = createTemplate(engine, 'assets/assetjs/application.js', addConf)
		writeToFile("grails-app/assets/javascripts/application.js",appjs.toString())
		
		def appcss = createTemplate(engine, 'assets/assetjs/application.css', addConf)
		writeToFile("grails-app/assets/stylesheets/application.css",appcss.toString())	
		
	}else if (appStyle.is('resources')) {
		dir = new File(arrestedPluginDir, "src/templates/fonts")
		if (!okToWrite("${basedir}/web-app/css/fonts")) {
			return
		}
		
		println "Creating fonts folder: web-app/fonts"
		copy(todir: new File(basedir, 'web-app/css/fonts')) {
			fileset dir: dir
		}
	
		def maingsp = createTemplate(engine, 'views/layouts/main.gsp', addConf)
		writeToFile("grails-app/views/layouts/main.gsp",maingsp.toString())
	
		/*
		dir = new File(arrestedPluginDir, "src/templates/views/css")
		println "Creating fonts folder: ${csspathController}"
		dir = new File(arrestedPluginDir, "src/templates/views/css")
		copy(todir: new File(basedir, csspathController)) {
			fileset dir: dir
		}*/
		
		// Unhappy with previous method lots of duplicate css files around 
		// New method first copies assets css files over 
		// uses new method to call file and parse it 
		
		//def replacements=['../images/':'images/','../fonts/':'fonts/']
		def replacements=['../fonts/':'fonts/']
		def glyphicon = fileToString('assets/stylesheets/bootstrap-glyphicons.css')
		parseToFile("web-app/css/bootstrap-glyphicons.css",glyphicon.toString(),replacements)
			
		def btstrp = fileToString('assets/stylesheets/bootstrap.css')
		parseToFile("web-app/css/bootstrap.css",btstrp.toString(),replacements)
		
		def fntawsome = fileToString('assets/stylesheets/font-awesome.css')
		parseToFile("web-app/css/font-awesome.css",fntawsome.toString(),replacements)
		
		copy(file:"$arrestedPluginDir/src/templates/views/js/application.js",
		tofile: "$basedir/web-app/js/application.js", overwrite: false)
		
		def indexgsp = createTemplate(engine, 'views/index.gsp', addConf)
		writeToFile("grails-app/views/index.gsp",indexgsp.toString())
	}
	
    depends(compile)
	
	println("main.gsp, index.gsp, arrested.css, _navbar.gsp updated")
}

target(createControllerGsp: "Create the angular controller.gsp template") {
	depends(compile)
	depends(loadApp)
	def (pkg, prefix) = parsePrefix()
	def domainClasses = grailsApp.domainClasses
	def names = []
	
	def engine = new SimpleTemplateEngine()
	domainClasses.each {
		domainClass ->
			if (domainClass.getShortName() != "ArrestedUser" && domainClass.getShortName() != "ArrestedToken") {
				names.add([propertyName: domainClass.getPropertyName(), className: domainClass.getShortName()])
			}
	}
	StringBuilder rul=new StringBuilder()
	def cpathUser=verifyGrailsVersion(appVersion, 'js', 'custom-'+appName)
	names.each {
		if (new File("${basedir}/${cpathUser}/${it.className}Ctrl.js").exists()) {
			rul.append('<shiro:hasPermission permission="').append(it.propertyName).append(':*">\n')
			rul.append('\t <a  ng-class="isSelected(\'').append(it.propertyName).append('\')')
			rul.append('? \'btn btn-primary\' :\'btn btn-default\'"  ng-click="setSelectedController(\'').append(it.propertyName).append('\')" ')
			rul.append('onclick=\'window.location.href="#/').append(it.propertyName).append('/list"\' ')
			rul.append('title="\${message(code: \'default.').append(it.propertyName).append('.label\', default: \'').append(it.className).append('\')}">\n')
			rul.append('\t\t<g:message code="default.').append(it.propertyName).append('}.label"  default="').append(it.className).append('"/>\n')
			rul.append('\t</a>\n')
			rul.append('</shiro:hasPermission>\n')
			
		}
	}
	def rulConf = [arrestedControllers: rul]
	def ruleCont = createTemplate(engine, 'views/layouts/_controllers.gsp', rulConf)
	writeToFile("grails-app/views/layouts/_controllers.gsp",ruleCont.toString())
	depends(compile)
    println("_controllers.gsp created ${basedir}/grails-app/views/layouts/_controllers.gsp")
}

okToWrite = { String dest ->
	def file = new File(dest)
	if (overwriteAll || !file.exists()) {
		return true
	}
	
	String propertyName = "file.overwrite.$file.name"
	ant.input(addProperty: propertyName, message: "$dest exists, ok to overwrite?", validargs: 'y,n,a', defaultvalue: 'y')
	
	if (ant.antProject.properties."$propertyName" == 'n') {
		return false
	}
	
	if (ant.antProject.properties."$propertyName" == 'a') {
		overwriteAll = true
	}
	true
}


localmkdir = { String dir ->
	if (!okToWrite(dir)) {
	return
	}
	def folders = new File(dir)
	folders.mkdirs()
}


writeToFile= { String file, String content ->
	if (!okToWrite(file)) {
		return
	}
	new File(basedir, "$file").write(content.toString())
}

parseToFile= { String file, String content,binding ->
	if (!okToWrite(file)) {
		return
	}
	
	binding.each { k,v ->
		content=content.toString().replace(k,v)
	}
	new File(basedir, "$file").write(content.toString())
}
private validateStyling(String appVersion) {
	double verify=getGrailsVersion(appVersion)
	if (verify >= 2.4 ) {
		return "assets"
	}else if (verify < 2.4 ) {
		return "resources" 
	}
}
private getGrailsVersion(String appVersion) { 
	if (appVersion && appVersion.indexOf('.')>-1) {
		int lastPos=appVersion.indexOf(".", appVersion.indexOf(".") + 1)
		double verify=appVersion.substring(0,lastPos) as double
	}	
}

private verifyGrailsVersion(String appVersion, String folder,String addon) {
	double verify=getGrailsVersion(appVersion)
	if (verify >= 2.4 ) { 
		if (folder.equals('js')) { folder="javascripts" }
		if (folder.equals('css')) { folder="stylesheets" }
		return "grails-app/assets/${folder}/${addon}"
	}else if (verify < 2.4 ) { 
		return "web-app/${folder}/"
	}
}

private createTemplate(SimpleTemplateEngine engine, relativePath, binding) {
	engine.createTemplate(new FileReader("$arrestedPluginDir/src/templates/$relativePath")).make(binding)
}
private fileToString(relativePath) {
	new FileReader("$arrestedPluginDir/src/templates/$relativePath").text
}


private parsePrefix() {
    def prefix = "Arrested"
    def pkg = ""
    if (argsMap['params'][0] != null) {
        def givenValue = argsMap['params'][0].split(/\./, -1)
        prefix = givenValue[-1]
        pkg = givenValue.size() > 1 ? givenValue[0..-2].join('.') : ""
    }
    return [pkg, prefix]
}


private parsePrefix1() {
	def prefix = "Arrested"
	def pkg = ""
	if (argsMap['params'][0] != null) {
		def givenValue = argsMap['params'][0].split(/\./, -1)
		prefix = givenValue[-1]
		pkg = givenValue.size() > 1 ? givenValue[0..-2].join('.') : ""
	}
	return [pkg ?: 'arrested', prefix]
}

private packageToPath(String pkg) {
    return pkg ? '/' + pkg.replace('.' as char, '/' as char) : ''
}

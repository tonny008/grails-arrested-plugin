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
    //def pluginM = PluginManagerHolder.pluginManager
    //def plugin = pluginM.getGrailsPlugin("hibernate")
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
	names.each {
		if (new File("${basedir}/web-app/js/${it.className}Ctrl.js").exists()) {
			rul.append('\t\tresource url: \'js/').append(it.className).append('Ctrl.js\'\n')
		}
	}
	def rulConf = [customAngularControllers: rul]
	def ruleCont = createTemplate(engine, 'configuration/ApplicationResources.groovy', rulConf)
	writeToFile("grails-app/conf/ApplicationResources.groovy",ruleCont.toString())
	   depends(compile)
    println("ApplicationResources.groovy updated")
}
target(createAngularService: "Create the angular service") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
    installTemplateEx("services.js", "web-app/js/${packageToPath(pkg)}", "views/controllers", "services.js") {}
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
    def configFile = new File("${basedir}/web-app/js/index.js")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
	def shortname= Metadata.current.'app.name'.toString().replaceAll(/(\_|\-|\.)/, '')
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "'use strict';"
        writer.writeLine "var " + shortname + " = angular.module('" + Metadata.current.'app.name' + "', ['services','ngRoute']);"
		//writer.writeLine "var " + shortname + " = angular.module('" + Metadata.current.'app.name' + "', ['services','ngRoute']);"
        writer.writeLine shortname + ".config([\n" +
                "    '\$routeProvider',\n" +
                "    function(\$routeProvider) {\n" +
                "        \$routeProvider."
        writer.writeLine "            when('/login', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showLogin', controller: 'UserCtrl'})."
		writer.writeLine "            when('/signup', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showSignup', controller: 'UserCtrl'})."
		writer.writeLine "            when('/updatepassword', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showUpdatePassword', controller: 'UserCtrl'})."
		writer.writeLine "            when('/updateusername', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showUpdateUsername', controller: 'UserCtrl'})."
		writer.writeLine "            when('/confirmupdate', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showUpdated', controller: 'UserCtrl'})."
		names.each {
			if (new File("${basedir}/web-app/js/${it.className}Ctrl.js").exists()) {
				writer.writeLine "            when('/" + it.propertyName + "/create', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/edit', controller: '" + it.className + "Ctrl'})."
				writer.writeLine "            when('/" + it.propertyName + "/edit', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/edit', controller: '" + it.className + "Ctrl'})."
				writer.writeLine "            when('/" + it.propertyName + "/list', {templateUrl: '/" + Metadata.current.'app.name' + "/"  + it.propertyName + "/listing', controller: '" + it.className + "Ctrl'})."
				writer.writeLine "            when('/" + it.propertyName + "', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/listing', controller: '" + it.className + "Ctrl'})."
			}	
        }
        writer.writeLine "            otherwise({redirectTo: '/login'});"
        writer.writeLine "    }"
        writer.writeLine "]);"
	 }
	
	 def addConf = [shortname: shortname]
	 def direc = createTemplate(engine, 'views/controllers/arrestedDirectives.js', addConf)
	 writeToFile("web-app/js/arrestedDirectives.js",direc.toString())

    depends(compile)
    println("index.js created")
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
    depends(compile)
    depends(loadApp)
    def (pkg, prefix) = parsePrefix()
    def className = prefix + "Ctrl"
    def domainClasses = grailsApp.domainClasses
    domainClasses.each {
        domainClass ->
            if (domainClass.getShortName() == prefix) {
                installTemplateEx("${className}.js", "web-app/js/${packageToPath(pkg)}", "views/controllers", "Controller.js") {
                    ant.replace(file: artefactFile) {
                        ant.replacefilter(token: '@controller.name@', value: className)
                        ant.replacefilter(token: '@class.name@', value: prefix)
                        ant.replacefilter(token: '@class.instance@', value: domainClass.getPropertyName())
                        ant.replacefilter(token: '@app.name@', value: Metadata.current.'app.name')
                    }
                }
            }
    }
    depends(compile)
}
target(createAngularUser: "Create the angular user controller") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
    installTemplateEx("userCtrl.js", "web-app/js/", "views/controllers", "userController.js") {
        ant.replace(file: artefactFile) {
            ant.replacefilter(token: '@app.name@', value: Metadata.current.'app.name')
        }
    }
    installTemplateEx("login.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "login.html") {}
	installTemplateEx("signup.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "signup.html") {}
	installTemplateEx("update.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "update.html") {}
	installTemplateEx("update-username.gsp", "grails-app/views/${packageToPath(pkg)}auth", "views/view", "update-username.html") {}
    println("userController.js and login.html signup.html created")
    depends(compile)
}
target(updateLayout: "Update the layout view") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
	def engine = new SimpleTemplateEngine()

	def shortname= Metadata.current.'app.name'.toString().replaceAll(/(\_|\-|\.)/, '')
	def addConf = [app: shortname]
	def maingsp = createTemplate(engine, 'views/layouts/main.gsp', addConf)
	writeToFile("grails-app/views/layouts/main.gsp",maingsp.toString())
	
	// Added to support 2.4 apps with resources plugin
	copy(file:"$arrestedPluginDir/src/templates/views/js/application.js",
		tofile: "$basedir/web-app/js/application.js", overwrite: false)
	
	copy(file:"$arrestedPluginDir/src/templates/views/js/ng-table.js",
		tofile: "$basedir/web-app/js/ng-table.js", overwrite: false)
	copy(file:"$arrestedPluginDir/src/templates/views/css/ng-table.css",
		tofile: "$basedir/web-app/css/ng-table.css", overwrite: false)

	def indexgsp = createTemplate(engine, 'views/index.gsp', addConf)
	writeToFile("grails-app/views/index.gsp",indexgsp.toString())
	
	def arrestedcss = createTemplate(engine, 'views/css/arrested.css', addConf)
	writeToFile("/web-app/css/arrested.css",arrestedcss.toString())
	

	def navbar = createTemplate(engine, 'views/layouts/_navbar.gsp', addConf)
	writeToFile("grails-app/views/layouts/_navbar.gsp",navbar.toString())
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
	names.each {
		if (new File("${basedir}/web-app/js/${it.className}Ctrl.js").exists()) {
			rul.append('\t <a  ng-class="isSelected(\'').append(it.propertyName).append('\')')
			rul.append('? \'btn btn-primary\' :\'btn btn-default\'"  ng-click="setSelectedController(\'').append(it.propertyName).append('\')" ')
			rul.append('onclick=\'window.location.href="#/').append(it.propertyName).append('/list"\' ')
			rul.append('title="\${message(code: \'default.').append(it.propertyName).append('.label\', default: \'').append(it.className).append('\')}">\n')
			rul.append('\t\t<g:message code="default.').append(it.propertyName).append('}.label"  default="').append(it.className).append('"/>\n')
			rul.append('\t</a>\n')
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

writeToFile= { String file, String content ->
	if (!okToWrite(file)) {
		return
	}
	new File(basedir, "$file").write(content.toString())
}

private createTemplate(SimpleTemplateEngine engine, relativePath, binding) {
	engine.createTemplate(new FileReader("$arrestedPluginDir/src/templates/$relativePath")).make(binding)
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
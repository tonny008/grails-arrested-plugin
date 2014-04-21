import grails.util.Holders
import grails.util.Metadata
import groovy.text.SimpleTemplateEngine
import groovy.text.Template

import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.springframework.core.io.ResourceLoader
includeTargets << grailsScript("_GrailsBootstrap")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsCompile")
overwriteAll = false
installTemplate = { String artefactName, String artefactPath, String templatePath ->
    installTemplateEx(artefactName, artefactPath, templatePath, artefactName, null)
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

installTemplateEx = { String artefactName, String artefactPath, String templatePath, String templateName, Closure c ->
    // Copy over the standard auth controller.
    def artefactFile = "${basedir}/${artefactPath}/${artefactName}"
	if (!okToWrite(artefactFile)) {
		return
	}
    /*if (new File(artefactFile).exists()) {
        ant.input(
                addProperty: "${args}.${artefactName}.overwrite",
                message: "${artefactName} already exists. Overwrite? [y/n]")

        if (ant.antProject.properties."${args}.${artefactName}.overwrite" == "n") {
            return
        }
    }*/

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
    /*if (new File(artefactFile).exists()) {
        ant.input(
                addProperty: "${args}.${artefactName}.overwrite",
                message: "${artefactName} already exists. Overwrite? [y/n]")

        if (ant.antProject.properties."${args}.${artefactName}.overwrite" == "n") {
            return
        }
    }*/
	
    // Copy the template file to the 'grails-app/controllers' directory.
    templateFile = "${arrestedPluginDir}/src/templates/${templatePath}/${templateName}"
    if (!new File(templateFile).exists()) {
        ant.echo("[Arrested plugin] Error: src/templates/${templatePath}/${templateName} does not exist!")
        return
    }

    renderEditorTemplate = engine.createTemplate(new File(templateFile))

    def pluginM = PluginManagerHolder.pluginManager
    def plugin = pluginM.getGrailsPlugin("hibernate")
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
    domainClasses.each {
        domainClass ->
            if (domainClass.getShortName() != "ArrestedUser" && domainClass.getShortName() != "ArrestedToken") {
                names.add([propertyName: domainClass.getPropertyName(), className: domainClass.getShortName()])
            }
    }
    def configFile = new File("${basedir}/grails-app/conf/ApplicationResources.groovy")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "modules = {"
        writer.writeLine "    application {"
        writer.writeLine "        dependsOn 'bootstrap'"
        writer.writeLine "        resource url:'js/application.js'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    bootstrap {"
        writer.writeLine "        dependsOn 'angularControllers'"
        writer.writeLine "        resource url:'http://netdna.bootstrapcdn.com/bootstrap/3.1.0/css/bootstrap.min.css'"
        writer.writeLine "        resource url:'http://netdna.bootstrapcdn.com/bootstrap/3.1.0/js/bootstrap.min.js'"
		writer.writeLine "        resource url:'http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap-glyphicons.css'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    angularControllers {"
        writer.writeLine "        dependsOn 'ngRoute'"
        writer.writeLine "        resource url:'js/userCtrl.js'"
        names.each {
            writer.writeLine "        resource url:'js/"+it.className+"Ctrl.js'"
        }
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    ngRoute {"
        writer.writeLine "        dependsOn 'angularConfiguration'"
        writer.writeLine "        resource url:'http://code.angularjs.org/snapshot/angular-route.min.js'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    angularConfiguration {"
        writer.writeLine "        dependsOn 'angularService'"
        writer.writeLine "        resource url: 'js/index.js'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    angularService {"
        writer.writeLine "        dependsOn 'angularResource'"
        writer.writeLine "        resource url: 'js/services.js'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    angularResource {"
        writer.writeLine "        dependsOn 'angular'"
        writer.writeLine "        resource url:'http://code.angularjs.org/snapshot/angular-resource.min.js'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    angular {"
        writer.writeLine "        dependsOn 'jQuery'"
        writer.writeLine "        resource url:'http://code.angularjs.org/snapshot/angular.min.js'"
        writer.writeLine "    }"
        writer.writeLine ""
        writer.writeLine "    jQuery {"
        writer.writeLine "        resource url:'http://code.jquery.com/jquery.min.js'"
        writer.writeLine "    }"
        writer.writeLine "}"
    }
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
        writer.writeLine shortname + ".config([\n" +
                "    '\$routeProvider',\n" +
                "    function(\$routeProvider) {\n" +
                "        \$routeProvider."
        writer.writeLine "            when('/login', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showLogin', controller: 'UserCtrl'})."
		writer.writeLine "            when('/signup', {templateUrl: '/" + Metadata.current.'app.name' + "/auth/showSignup', controller: 'UserCtrl'})."
        names.each {
            writer.writeLine "            when('/" + it.propertyName + "/create', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/edit', controller: '" + it.className + "Ctrl'})."
            writer.writeLine "            when('/" + it.propertyName + "/edit', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/edit', controller: '" + it.className + "Ctrl'})."
            writer.writeLine "            when('/" + it.propertyName + "/list', {templateUrl: '/" + Metadata.current.'app.name' + "/"  + it.propertyName + "/listing', controller: '" + it.className + "Ctrl'})."
            writer.writeLine "            when('/" + it.propertyName + "', {templateUrl: '/" + Metadata.current.'app.name' + "/" + it.propertyName + "/listing', controller: '" + it.className + "Ctrl'})."
        }
        writer.writeLine "            otherwise({redirectTo: '/login'});"
        writer.writeLine "    }"
        writer.writeLine "]);"
		
		// Password Matching directive added 0.10
		writer.writeLine shortname + ".directive('passwordMatch', [function () {"
		writer.writeLine "\treturn {"
		writer.writeLine "\trestrict: 'A',"
		writer.writeLine "\tscope:true,"
		writer.writeLine "\trequire: 'ngModel',"
		writer.writeLine "\tlink: function (scope, elem , attrs,control) {"
		writer.writeLine "\t\tvar checker = function () {"
		writer.writeLine "\t\t//get the value of the first password"
		writer.writeLine "\t\tvar e1 = scope.\$eval(attrs.ngModel);"
		writer.writeLine "\t\t//get the value of the other password"
		writer.writeLine "\t\tvar e2 = scope.\$eval(attrs.passwordMatch);"
		writer.writeLine "\t\treturn e1 == e2;"
		writer.writeLine "\t};"
		writer.writeLine "\tscope.\$watch(checker, function (n) {"
		writer.writeLine "\t\t//set the form control to valid if both"
		writer.writeLine "\t\t//passwords are the same, else invalid"
		writer.writeLine "\t\tcontrol.\$setValidity('unique', n);"
		writer.writeLine "\t});"
		writer.writeLine "\t}"
		writer.writeLine "\t};"
		writer.writeLine "}]);"
	
		// Unique username directive - needs work
		/*
		writer.writeLine shortname + ".directive('uniqueEmail', [\"ArrestedUser\", function (ArrestedUser) {"
		writer.writeLine """	return {
			  require:'ngModel',
			  restrict:'A',
			  link:function (scope, el, attrs, ctrl) {
				ctrl.\$parsers.push(function (viewValue) {
		  
				  if (viewValue) {
					ArrestedUser.query({username:viewValue}, function (users) {
					  if (users.length === 0) {
						ctrl.\$setValidity('uniqueEmail', true);
					  } else {
						ctrl.\$setValidity('uniqueEmail', false);
					  }
					});
					return viewValue;
				  }
				});
			  }
			};
		  }])
		"""*/
    }
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
    println("userController.js and login.html signup.html created")
    depends(compile)
}
target(updateLayout: "Update the layout view") {
    depends(compile)
    def (pkg, prefix) = parsePrefix()
    def configFile = new File("${basedir}/grails-app/views/layouts/main.gsp")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "<!DOCTYPE html>\n" +
                "<html lang=\"en\" data-ng-app=\"${Metadata.current.'app.name'}\">\n" +
                "<head>\n" +
                "    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\">\n" +
                "    <title><g:layoutTitle default=\"Arrested\"/></title>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link rel=\"shortcut icon\" href=\"\${resource(dir: 'images', file: 'favicon.ico')}\" type=\"image/x-icon\">\n" +
                "    <link rel=\"apple-touch-icon\" href=\"\${resource(dir: 'images', file: 'apple-touch-icon.png')}\">\n" +
                "    <link rel=\"apple-touch-icon\" sizes=\"114x114\" href=\"\${resource(dir: 'images', file: 'apple-touch-icon-retina.png')}\">\n" +
                "    <link rel=\"stylesheet\" href=\"\${resource(dir: 'css', file: 'main.css')}\" type=\"text/css\">\n" +
                "    <link rel=\"stylesheet\" href=\"\${resource(dir: 'css', file: 'arrested.css')}\" type=\"text/css\">\n" +
                "    <link rel=\"stylesheet\" href=\"\${resource(dir: 'css', file: 'mobile.css')}\" type=\"text/css\">\n" +
                "    <r:require module='application'/>\n" +
                "    <g:layoutHead/>\n" +
                "    <r:layoutResources />\n" +
                "</head>\n" +
                "<body>\n" +
                "<div id=\"arrestedHeader\" role=\"banner\"><h1 id=\"h1Header\"><g:message code=\"default.welcome.title\" args=\"[meta(name:'app.name')]\"/></h1></div>\n" +
                "<g:render template=\"/layouts/navbar\"/>\n" +
                "<g:layoutBody/>\n" +
                "<div class=\"footer\" role=\"contentinfo\"></div>\n" +
                "<div id=\"spinner\" class=\"spinner\" style=\"display:none;\"><g:message code=\"spinner.alt\" default=\"Loading&hellip;\"/></div>\n" +
                "<r:layoutResources />\n" +
                "</body>\n" +
                "</html>"
    }

    configFile = new File("${basedir}/grails-app/views/index.gsp")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\t<head>\n" +
                "\t\t<meta name=\"layout\" content=\"main\"/>\n" +
                "\t\t<title><g:message code=\"default.welcome.title\" args=\"[meta(name:'app.name')]\"/></title>\n" +
                "\t\t<style type=\"text/css\" media=\"screen\">\n" +
                "\t\t\t#status {\n" +
                "\t\t\t\tbackground-color: #eee;\n" +
                "\t\t\t\tborder: .2em solid #fff;\n" +
                "\t\t\t\tmargin: 2em 2em 1em;\n" +
                "\t\t\t\tpadding: 1em;\n" +
                "\t\t\t\twidth: 12em;\n" +
                "\t\t\t\tfloat: left;\n" +
                "\t\t\t\t-moz-box-shadow: 0px 0px 1.25em #ccc;\n" +
                "\t\t\t\t-webkit-box-shadow: 0px 0px 1.25em #ccc;\n" +
                "\t\t\t\tbox-shadow: 0px 0px 1.25em #ccc;\n" +
                "\t\t\t\t-moz-border-radius: 0.6em;\n" +
                "\t\t\t\t-webkit-border-radius: 0.6em;\n" +
                "\t\t\t\tborder-radius: 0.6em;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t.ie6 #status {\n" +
                "\t\t\t\tdisplay: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#status ul {\n" +
                "\t\t\t\tfont-size: 0.9em;\n" +
                "\t\t\t\tlist-style-type: none;\n" +
                "\t\t\t\tmargin-bottom: 0.6em;\n" +
                "\t\t\t\tpadding: 0;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#status li {\n" +
                "\t\t\t\tline-height: 1.3;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#status h1 {\n" +
                "\t\t\t\ttext-transform: uppercase;\n" +
                "\t\t\t\tfont-size: 1.1em;\n" +
                "\t\t\t\tmargin: 0 0 0.3em;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#page-body {\n" +
                "                margin: 1% 5%;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\th2 {\n" +
                "\t\t\t\tmargin-top: 1em;\n" +
                "\t\t\t\tmargin-bottom: 0.3em;\n" +
                "\t\t\t\tfont-size: 1em;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\tp {\n" +
                "\t\t\t\tline-height: 1.5;\n" +
                "\t\t\t\tmargin: 0.25em 0;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#controller-list ul {\n" +
                "\t\t\t\tlist-style-position: inside;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t#controller-list li {\n" +
                "\t\t\t\tline-height: 1.3;\n" +
                "\t\t\t\tlist-style-position: inside;\n" +
                "\t\t\t\tmargin: 0.25em 0;\n" +
                "\t\t\t}\n" +
                "\n" +
                "\t\t\t@media screen and (max-width: 480px) {\n" +
                "\t\t\t\t#status {\n" +
                "\t\t\t\t\tdisplay: none;\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t#page-body {\n" +
                "\t\t\t\t\tmargin: 0 1em 1em;\n" +
                "\t\t\t\t}\n" +
                "\n" +
                "\t\t\t\t#page-body h1 {\n" +
                "\t\t\t\t\tmargin-top: 0;\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}\n" +
                "\t\t</style>\n" +
                "\t</head>\n" +
                "\t<body>\n" +
                "\t\t<div class=\"row\" id=\"page-body\" role=\"main\">\n" +
                "            <div class=\"content\" role=\"main\" data-ng-view>\n" +
                "            </div>\n" +
                "\t\t</div>\n" +
                "\t</body>\n" +
                "</html>"
    }

    configFile = new File("${basedir}/web-app/css/arrested.css")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "#h1Header {\n" +
                "    font-size: 2.25em !important;\n" +
                "    text-align: center !important;\n" +
                "    padding: 10px !important;\n" +
                "}\n" +
                ".nav {\n" +
                "    min-height: 30px !important;\n" +
                "}\n" +
                "#arrestedHeader{\n" +
                "    background-color: #C8CCBE;\n" +
                "}\n" +
                ".footer{\n" +
                "    background: #C8CCBE !important;\n" +
                "}\n" +
                ".red{\n" +
                "    color: red;\n" +
                "}\n" +
                "a:link, a:visited, a:hover {\n" +
                "    color: #000000 !important;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "    margin: 0 5% 0 5% !important;\n" +
                "    max-width: 100% !important;\n" +
                "}"
				writer.writeLine """
.left-inner-addon {
	position: relative;
}
.left-inner-addon input {
	padding-left: 30px;
}
.left-inner-addon i {
	position: absolute;
	padding: 10px 12px;
	pointer-events: none;
}
.right-inner-addon {
	position: relative;
}
.right-inner-addon input {
	padding-right: 30px;    
}
.right-inner-addon i {
	position: absolute;
	right: 0px;
	padding: 10px 12px;
	pointer-events: none;
}
"""
    }

    configFile = new File("${basedir}/grails-app/views/layouts/_navbar.gsp")
    if (configFile.exists()) {
        configFile.delete()
    }
    configFile.createNewFile()
    configFile.withWriterAppend { BufferedWriter writer ->
        writer.writeLine "<div class=\"container row\" data-ng-controller=\"UserCtrl\" data-ng-show=\"appConfig.token!=''\">\n" +
                "    <div class=\"col-md-12\">\n" +
                "        <p></p>\n" +
                "        <ul class=\"nav navbar-nav col-md-12\" style=\"min-height: 30px;\">\n" +
                "            <g:each var=\"c\" in=\"\${grailsApplication.controllerClasses.sort { it.fullName } }\">\n" +
                "                <g:if test=\"\${!(c.fullName.contains('DbdocController')||c.fullName.contains('ArrestedUser')||c.fullName.contains('ArrestedController')||c.fullName.contains('AuthController'))}\">\n" +
                "                    <li class=\"controller\">\n" +
                "                        <a onclick='window.location.href=\"#/\${c.logicalPropertyName}/list\"'>\n" +
                "                            \${c.name}\n" +
                "                        </a>\n" +
                "                    </li>\n" +
                "                </g:if>\n" +
                "            </g:each>\n" +
				"	 		<li class='controller'>"
				writer.writeLine """<a data-ng-controller='UserCtrl' data-ng-click='logout()' title="\${message(code: 'security.signoff.label', default: 'Log out')}">
								<span class="glyphicon glyphicon-log-out"></span> <g:message code="security.signoff.label"/>
							</a>
							</li>
						</ul>
					</div>
				</div>
"""
}
    depends(compile)
    println("main.gsp, index.gsp, arrested.css, _navbar.gsp updated")
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
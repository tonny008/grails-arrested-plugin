includeTargets << grailsScript("_GrailsArgParsing")
//includeTargets << grailsScript('_GrailsPackage')
includeTargets << new File (arrestedPluginDir, "scripts/_ArrestedInternal.groovy")

USAGE = """
   grails  arrested [NAME]
"""



target(createArrestedView: "This Controller, GSP mapped at index (view is index.gsp) AND the javascript controller") {
	depends(parseArguments, createController)
	depends(parseArguments, createViewController)
	
	depends(parseArguments, createJSController)
	depends(parseArguments, createAngularIndex)
	depends(parseArguments, updateResources)
	/*
	depends(compile)
	depends(loadApp)
	def domainClasses = grailsApp.domainClasses
	domainClasses.each {
		domainClass ->
		def pkg=domainClass.getPackageName()
		def fn=domainClass.getFullName()
		def className = domainClass.getPropertyName()
		if (className.endsWith( argsMap['params'][0])) {
			depends(fn, createController)
			depends(className, createViewController)
			
			depends(className, createJSController)
			depends(className, createAngularIndex)
			depends(className, updateResources)
		}
	}*/
}

setDefaultTarget 'createArrestedView'

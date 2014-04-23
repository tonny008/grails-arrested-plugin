includeTargets << grailsScript("_GrailsArgParsing")
includeTargets << new File (arrestedPluginDir, "scripts/_ArrestedInternal.groovy")

USAGE = """
   grails  arrested [NAME]
"""


target(createArrestedView: "This creates the GSP mapped at index (view is index.gsp) AND the javascript controller") {
	depends(parseArguments, createController)
    depends(parseArguments, createViewController)
    depends(parseArguments, createJSController)
    depends(parseArguments, createAngularIndex)
    depends(parseArguments, updateResources)
}

setDefaultTarget 'createArrestedView'

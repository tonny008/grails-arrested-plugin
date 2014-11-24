@package.line@
class SecurityFilters {
/**
	 * Array of controller/action combinations which will be skipped from authentication
	 * if the controller and action names match. The action value can also be '*' if it
	 * encompasses all actions within the controller.
	 */
	static nonAuthenticatedActions = [
			[controller: 'auth', action: '*'],
			[controller: 'arrestedUser',action: 'save'],
			
	]
    def filters = {
        arrested(uri: "/**/#/**") {
            before = {

		def needsAuth = !nonAuthenticatedActions.find {
					(it.controller == controllerName) &&
							((it.action == '*') || (it.action == actionName))
		}

                if(!needsAuth || !controllerName){
                    return true
                }

	       accessControl()	
				
			}
		}
	}
}	

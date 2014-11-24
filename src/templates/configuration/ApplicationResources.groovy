modules = {
    application {
        dependsOn 'bootstrap'
        resource url:'js/application.js'
        
    }

    bootstrap {
        dependsOn 'angularControllers'
        resource url:'css/bootstrap.css'
        resource url:'js/bootstrap/bootstrap.js'
        resource url:'css/bootstrap-glyphicons.css'
        resource url:'css/font-awesome.css'
    }
	angularControllers {
		dependsOn 'ngRoute'
		resource url:'js/arrestedDirectives.js'
		resource url:'js/dashboardCtrl.js'
		resource url:'js/userCtrl.js'
		resource url:'js/clockCtrl.js'
$customAngularControllers
	}
 
   
    ngRoute {
        dependsOn 'angularConfiguration'
        resource url:'js/angular-route/angular-route.js'
    }

    angularConfiguration {
        dependsOn 'angularService'
        resource url: 'js/index.js'
    }

    angularService {
        dependsOn 'angularResource', 'angularNgTable', 'angularStrap', 'angularCookies','arrestedServices'
        resource url: 'js/services.js'	
    }
	
	arrestedServices {
		resource url:'js/arrestedServices.js'
	}
	
    angularResource {
        dependsOn 'angular'
        resource url:'js/angular-resource/angular-resource.min.js'
    }
	
	angularNgTable {
		resource url:'js/angular-table/ng-table.js'
		resource url:'css/ng-table.css'
	}
	
	angularStrap { 
		resource url:'js/angular-strap/angular-strap.js'
	}
	
	angularCookies {
		resource url:'js/angular-cookies/angular-cookies.js'
	}

    angular {
        dependsOn 'jQuery'
        resource url:'js/angular/angular.js'
    }

    jQuery {
        resource url:'js/jquery/jquery.js'
    }
}

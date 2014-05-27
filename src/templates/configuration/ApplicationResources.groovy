modules = {
    application {
        dependsOn 'bootstrap'
        resource url:'js/application.js'
        
    }

    bootstrap {
        dependsOn 'angularControllers'
        resource url:'css/bootstrap.min.css'
        resource url:'js/bootstrap/bootstrap.js'
        resource url:'css/bootstrap-glyphicons.css'
        resource url:'css/font-awesome.css'
    }
	angularControllers {
		dependsOn 'ngRoute'
		resource url:'js/arrestedDirectives.js'
		resource url:'js/userCtrl.js'
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
        dependsOn 'angularResource', 'angularNgTable'
        resource url: 'js/services.js'
    }

    angularResource {
        dependsOn 'angular'
        resource url:'js/angular-resource/angular-resource.min.js'
    }
	
	angularNgTable {
		resource url:'js/angular-table/ng-table.js'
		resource url:'css/ng-table.css'
	}
	
    angular {
        dependsOn 'jQuery'
        resource url:'js/angular/angular.js'
    }

    jQuery {
        resource url:'js/jquery/jquery.js'
    }
}

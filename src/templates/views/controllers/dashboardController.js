'use strict';

function DashboardCtrl(\$cookieStore,\$scope,\$rootScope,LangService,\$cacheFactory,\$templateCache) {
	
    \$scope.dashboard=function() { 
    	/*
    	\$templateCache.removeAll();
    	\$cacheFactory.get('\$http').removeAll();
    	*/


    	\$rootScope.appConfig = {appName:'$appName', token:''}; 
    	\$rootScope.user = {id:'', username:'', passwordHash:''}; 
    	if(\$cookieStore.get('token')) { 
    		\$rootScope.appConfig.token = \$cookieStore.get('token'); 
    		\$rootScope.user.id = \$cookieStore.get('id'); 
    		\$rootScope.user.username = \$cookieStore.get('username'); 
    	}

    	
    	
    	LangService.getLang()
    	.then(function(data) {
		  \$rootScope.myLang = data;
    	});
    	\$scope.userLocale2 = function() {
    		LangService.getLang2()
    		.then(function(data) {
    			\$scope.userLocale = data;
    		});
    	//window.location.href="#/"
    	};	
    }
}

'use strict';
function @controller.name@(DAO, $rootScope, $scope, $filter, ngTableParams)
{
	if ($rootScope.appConfig) {
		if (!$rootScope.appConfig.token!='') {
			window.location.href = "#/login"
		}
	}

	$rootScope.flags = {save: false};
	$rootScope.errors = {loadingSite: false, showErrors: false, showServerError: false,errorMessages:[]};
	$rootScope.errorValidation = function(){
	   $rootScope.errors = {loadingSite: true};
	};
	
	if(!$rootScope.@class.instance@){
		$rootScope.filter = ""
		$rootScope.@class.instance@s = [];
		$rootScope.@class.instance@ = {};
	}

	$rootScope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,           // count per page
        sorting: {
            id : 'desc' // initial sorting
        }
	}, {
		getData: function($defer, params) {
			DAO.query({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, controller: '@class.instance@', action: 'list'},	
				$rootScope.loadingSite=true,
					function (result) {
						$rootScope.@class.instance@s=result;
						var putIt  = params.sorting() ? $filter('orderBy')($rootScope.@class.instance@s, params.orderBy()): id;
						putIt = params.filter ? $filter('filter')( putIt, params.filter()) :  putIt;
						params.total(putIt.length);
						$defer.resolve(putIt.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						$rootScope.@class.instance@s=(putIt.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						$rootScope.loadingSite=false;   
					},
					function (error) {
						$rootScope.errors.showErrors = true;
						$rootScope.errors.showServerError = true;
						$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
						$rootScope.loadingSite=false;
					});
      	}
    });
	// @deprecated
	$rootScope.getAll@class.name@ = function () {
		//get all
		$rootScope.errors.errorMessages=[];
		 DAO.query({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, controller: '@class.instance@', action: 'list'},	
		 	$rootScope.loadingSite=true,
		 	function (result) {
			 	$rootScope.tableParams = new ngTableParams({
			 		page: 1,            // show first page
			 		count: 10,           // count per page
			 		sorting: {
			 			id : 'desc' // initial sorting
			 		}
			 	}, {
			 		total: result.length,
			 		getData: function($defer, params) {
			 			$rootScope.@class.instance@s  = params.sorting() ? $filter('orderBy')(result, params.orderBy()): id;
			 			//params.total(putIt.length);
			 			$defer.resolve($rootScope.@class.instance@s.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			 			$rootScope.@class.instance@s=($rootScope.@class.instance@s.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			 		}
			 	});
		        $rootScope.loadingSite=false;   
		    },
		    function (error) {
		        $rootScope.errors.showErrors = true;
		        $rootScope.errors.showServerError = true;
		        $rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
		        $rootScope.loadingSite=false;
		     });
		       
	};
	 
	/*
	$rootScope.getAllOld@class.name@ = function () {
		//get all
		$rootScope.errors.errorMessages=[];
		DAO.query({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, controller: '@class.instance@', action: 'list'},
		$rootScope.loadingSite=true,
		function (result) {
			$rootScope.@class.instance@s = result;
			$rootScope.loadingSite=false;   
			
		},
		function (error) {
			$rootScope.errors.showErrors = true;
			$rootScope.errors.showServerError = true;
			$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			$rootScope.loadingSite=false;
		});
	};
	 */
	$rootScope.new@class.name@ = function () {
		$rootScope.loadingSite=true;
		$rootScope.@class.instance@ = {};
		$rootScope.loadingSite=false;
		window.location.href = "#/@class.instance@/create"		
	}

	$rootScope.manualSave@class.name@ = function () {
		$rootScope.loadingSite=true;
		$rootScope.flags.save = false;
		if ($rootScope.@class.instance@.id == undefined)
		{
			$rootScope.save@class.name@();
		}
		else
		{
			$rootScope.update@class.name@();
		}
	}

	$rootScope.save@class.name@ = function () {
		$rootScope.errors.errorMessages=[];
		DAO.save({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, controller:'@class.instance@', action:'save'},
		function (result) {
			$rootScope.@class.instance@ = result;
			$rootScope.flags.save = true;
			$rootScope.loadingSite=false;

		},
		function (error) {
			$rootScope.flags.save = false;
			$rootScope.errors.showErrors = true;
			$rootScope.errors.showServerError = true;
			$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			$rootScope.loadingSite=false;   
		});
	}

	$rootScope.update@class.name@ = function () {
		$rootScope.errors.errorMessages=[];
		DAO.update({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, controller:'@class.instance@', action:'update'},
		$rootScope.loadingSite=true,
		function (result) {
			$rootScope.@class.instance@s = result;
			$rootScope.flags.save = true;
			$rootScope.loadingSite=false;
			window.location.href = "#/@class.instance@/list"
		},
		function (error) {
			$rootScope.flags.save = false;
			$rootScope.errors.showErrors = true;
			$rootScope.errors.showServerError = true;
			$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			$rootScope.loadingSite=false;
		});
	}

	$rootScope.edit@class.name@ = function (@class.instance@){
		$rootScope.errors.errorMessages=[];
		DAO.get({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, id: @class.instance@.id, controller:'@class.instance@', action:'show'},
		$rootScope.loadingSite=true,
		function (result) {
			$rootScope.@class.instance@ = result;
			$rootScope.flags.save = true;
			$rootScope.loadingSite=false;
			window.location.href = "#/@class.instance@/edit"
		},
		function (error) {
			$rootScope.errors.showErrors = true;
			$rootScope.errors.showServerError = true;
			$rootScope.errors.errorMessages.push('Error: '+error.status+' '+error.data);
			$rootScope.loadingSite=false;
		});
	}

	$rootScope.confirmDelete@class.name@ = function () {
		$rootScope.errors.errorMessages=[];
		DAO.delete({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, id: $rootScope.@class.instance@.id, controller:'@class.instance@', action:'delete'},
		$rootScope.loadingSite=true,
		function (result) {
			//$rootScope.@class.instance@s = result;
			$rootScope.flags.save = true;
			$rootScope.loadingSite=false;
			window.location.href = "#/@class.instance@/list"
		},
		function (error) {
			$rootScope.errors.showErrors = true;
			$rootScope.errors.showServerError = true;
			$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			$rootScope.loadingSite=false;
		});
	}
}

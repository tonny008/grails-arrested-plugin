'use strict';
function $contName(DAO, \$rootScope, \$scope, \$filter, ngTableParams)
{
	if (\$rootScope.appConfig) {
		if (!\$rootScope.appConfig.token!='') {
			window.location.href = "#/login"
		}
	}

	\$rootScope.flags = {save: false};
	\$rootScope.errors = {loadingSite: false, showErrors: false, showServerError: false,errorMessages:[]};
	\$rootScope.errorValidation = function(){
	   \$rootScope.errors = {loadingSite: true};
	};
	
	if(!\$rootScope.${instance}){
		\$rootScope.filter = ""
		\$rootScope.${instance}s = [];
		\$rootScope.${instance} = {};
	}

	\$rootScope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,           // count per page
        sorting: {
            id : 'desc' // initial sorting
        }
	}, {
		getData: function(\$defer, params) {
			DAO.query({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, controller: '${instance}', action: 'list'},	
				\$rootScope.loadingSite=true,
					function (result) {
						\$rootScope.${instance}s=result;
						var putIt  = params.sorting() ? \$filter('orderBy')(\$rootScope.${instance}s, params.orderBy()): id;
						putIt = params.filter ? \$filter('filter')( putIt, params.filter()) :  putIt;
						params.total(putIt.length);
						\$defer.resolve(putIt.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						\$rootScope.${instance}s=(putIt.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						\$rootScope.loadingSite=false;   
					},
					function (error) {
						\$rootScope.errors.showErrors = true;
						\$rootScope.errors.showServerError = true;
						\$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
						\$rootScope.loadingSite=false;
					});
      	}
    });
	// @deprecated
	\$rootScope.getAll$className = function () {
		//get all
		\$rootScope.errors.errorMessages=[];
		 DAO.query({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, controller: '${instance}', action: 'list'},	
		 	\$rootScope.loadingSite=true,
		 	function (result) {
			 	\$rootScope.tableParams = new ngTableParams({
			 		page: 1,            // show first page
			 		count: 10,           // count per page
			 		sorting: {
			 			id : 'desc' // initial sorting
			 		}
			 	}, {
			 		total: result.length,
			 		getData: function(\$defer, params) {
			 			\$rootScope.${instance}s  = params.sorting() ? \$filter('orderBy')(result, params.orderBy()): id;
			 			//params.total(putIt.length);
			 			\$defer.resolve(\$rootScope.${instance}s.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			 			\$rootScope.${instance}s=(\$rootScope.${instance}s.slice((params.page() - 1) * params.count(), params.page() * params.count()));
			 		}
			 	});
		        \$rootScope.loadingSite=false;   
		    },
		    function (error) {
		        \$rootScope.errors.showErrors = true;
		        \$rootScope.errors.showServerError = true;
		        \$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
		        \$rootScope.loadingSite=false;
		     });
		       
	};
	 
	/*
	\$rootScope.getAllOld$className = function () {
		//get all
		\$rootScope.errors.errorMessages=[];
		DAO.query({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, controller: '${instance}', action: 'list'},
		\$rootScope.loadingSite=true,
		function (result) {
			\$rootScope.${instance}s = result;
			\$rootScope.loadingSite=false;   
			
		},
		function (error) {
			\$rootScope.errors.showErrors = true;
			\$rootScope.errors.showServerError = true;
			\$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$rootScope.loadingSite=false;
		});
	};
	 */
	\$rootScope.new$className = function () {
		\$rootScope.loadingSite=true;
		\$rootScope.${instance} = {};
		\$rootScope.loadingSite=false;
		window.location.href = "#/${instance}/create"		
	}

	\$rootScope.manualSave$className = function () {
		\$rootScope.loadingSite=true;
		\$rootScope.flags.save = false;
		if (\$rootScope.${instance}.id == undefined)
		{
			\$rootScope.save$className();
		}
		else
		{
			\$rootScope.update$className();
		}
	}

	\$rootScope.save$className = function () {
		\$rootScope.errors.errorMessages=[];
		DAO.save({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, instance:\$rootScope.${instance}, controller:'${instance}', action:'save'},
		function (result) {
			\$rootScope.${instance} = result;
			\$rootScope.flags.save = true;
			\$rootScope.loadingSite=false;

		},
		function (error) {
			\$rootScope.flags.save = false;
			\$rootScope.errors.showErrors = true;
			\$rootScope.errors.showServerError = true;
			\$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$rootScope.loadingSite=false;   
		});
	}

	\$rootScope.update$className = function () {
		\$rootScope.errors.errorMessages=[];
		DAO.update({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, instance:\$rootScope.${instance}, controller:'${instance}', action:'update'},
		\$rootScope.loadingSite=true,
		function (result) {
			\$rootScope.${instance}s = result;
			\$rootScope.flags.save = true;
			\$rootScope.loadingSite=false;
			window.location.href = "#/${instance}/list"
		},
		function (error) {
			\$rootScope.flags.save = false;
			\$rootScope.errors.showErrors = true;
			\$rootScope.errors.showServerError = true;
			\$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$rootScope.loadingSite=false;
		});
	}

	\$rootScope.edit$className = function (${instance}){
		\$rootScope.errors.errorMessages=[];
		DAO.get({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, instance:\$rootScope.${instance}, id: ${instance}.id, controller:'${instance}', action:'show'},
		\$rootScope.loadingSite=true,
		function (result) {
			\$rootScope.${instance} = result;
			\$rootScope.flags.save = true;
			\$rootScope.loadingSite=false;
			window.location.href = "#/${instance}/edit"
		},
		function (error) {
			\$rootScope.errors.showErrors = true;
			\$rootScope.errors.showServerError = true;
			\$rootScope.errors.errorMessages.push('Error: '+error.status+' '+error.data);
			\$rootScope.loadingSite=false;
		});
	}

	\$rootScope.confirmDelete$className = function () {
		\$rootScope.errors.errorMessages=[];
		DAO.delete({appName: \$rootScope.appConfig.appName, token: \$rootScope.appConfig.token, instance:\$rootScope.${instance}, id: \$rootScope.${instance}.id, controller:'${instance}', action:'delete'},
		\$rootScope.loadingSite=true,
		function (result) {
			//\$rootScope.${instance}s = result;
			\$rootScope.flags.save = true;
			\$rootScope.loadingSite=false;
			window.location.href = "#/${instance}/list"
		},
		function (error) {
			\$rootScope.errors.showErrors = true;
			\$rootScope.errors.showServerError = true;
			\$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$rootScope.loadingSite=false;
		});
	}
}

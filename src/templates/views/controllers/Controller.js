'use strict';
function $contName(DAO, \$scope, \$filter, ngTableParams)
{
	if (\$scope.appConfig) {
		if (!\$scope.appConfig.token!='') {
			window.location.href = "#/login"
		}
	}

	\$scope.flags = {save: false};
	\$scope.errors = {loadingSite: false, showErrors: false, showServerError: false,errorMessages:[]};
	\$scope.errorValidation = function(){
	   \$scope.errors = {loadingSite: true};
	};
	
	if(!\$scope.${instance}){
		\$scope.filter = ""
		\$scope.${instance}s = [];
		\$scope.${instance} = {};
	}

	\$scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        count: 10,           // count per page
        sorting: {
            id : 'desc' // initial sorting
        }
	}, {
		getData: function(\$defer, params) {
			DAO.query({appName: \$scope.appConfig.appName, token: \$scope.appConfig.token, controller: '${instance}', action: 'list'},	
				\$scope.loadingSite=true,
					function (result) {
						\$scope.${instance}s=result;
						var putIt  = params.sorting() ? \$filter('orderBy')(\$scope.${instance}s, params.orderBy()): id;
						putIt = params.filter ? \$filter('filter')( putIt, params.filter()) :  putIt;
						params.total(putIt.length);
						\$defer.resolve(putIt.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						\$scope.${instance}s=(putIt.slice((params.page() - 1) * params.count(), params.page() * params.count()));
						\$scope.loadingSite=false;   
					},
					function (error) {
						\$scope.errors.showErrors = true;
						\$scope.errors.showServerError = true;
						\$scope.errors.errorMessages.push(''+error.status+' '+error.data);
						\$scope.loadingSite=false;
					});
      	}
    });
	
	//Required for dependency lookup 
	\$scope.getAll$className = function () {
		//get all
		\$scope.errors.errorMessages=[];
		DAO.query({appName: \$scope.appConfig.appName, token: \$scope.appConfig.token, controller: '${instance}', action: 'list'},
		\$scope.loadingSite=true,
		function (result) {
			\$scope.${instance}s = result;
			\$scope.loadingSite=false;   
			
		},
		function (error) {
			\$scope.errors.showErrors = true;
			\$scope.errors.showServerError = true;
			\$scope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$scope.loadingSite=false;
		});
	};
	 
	
	\$scope.new$className = function () {
		\$scope.loadingSite=true;
		\$scope.${instance} = {};
		\$scope.loadingSite=false;
		window.location.href = "#/${instance}/create"		
	}

	\$scope.manualSave$className = function () {
		\$scope.loadingSite=true;
		\$scope.flags.save = false;
		if (\$scope.${instance}.id == undefined)
		{
			\$scope.save$className();
		}
		else
		{
			\$scope.update$className();
		}
	}

	\$scope.save$className = function () {
		\$scope.errors.errorMessages=[];
		DAO.save({appName: \$scope.appConfig.appName, token: \$scope.appConfig.token, instance:\$scope.${instance}, controller:'${instance}', action:'save'},
		function (result) {
			\$scope.${instance} = result;
			\$scope.flags.save = true;
			\$scope.loadingSite=false;

		},
		function (error) {
			\$scope.flags.save = false;
			\$scope.errors.showErrors = true;
			\$scope.errors.showServerError = true;
			\$scope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$scope.loadingSite=false;   
		});
	}

	\$scope.update$className = function () {
		\$scope.errors.errorMessages=[];
		DAO.update({appName: \$scope.appConfig.appName, token: \$scope.appConfig.token, instance:\$scope.${instance}, controller:'${instance}', action:'update'},
		\$scope.loadingSite=true,
		function (result) {
			\$scope.${instance}s = result;
			\$scope.flags.save = true;
			\$scope.loadingSite=false;
			window.location.href = "#/${instance}/list"
		},
		function (error) {
			\$scope.flags.save = false;
			\$scope.errors.showErrors = true;
			\$scope.errors.showServerError = true;
			\$scope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$scope.loadingSite=false;
		});
	}

	\$scope.edit$className = function (${instance}){
		\$scope.errors.errorMessages=[];
		DAO.get({appName: \$scope.appConfig.appName, token: \$scope.appConfig.token, instance:\$scope.${instance}, id: ${instance}.id, controller:'${instance}', action:'show'},
		\$scope.loadingSite=true,
		function (result) {
			\$scope.${instance} = result;
			\$scope.flags.save = true;
			\$scope.loadingSite=false;
			window.location.href = "#/${instance}/edit"
		},
		function (error) {
			\$scope.errors.showErrors = true;
			\$scope.errors.showServerError = true;
			\$scope.errors.errorMessages.push('Error: '+error.status+' '+error.data);
			\$scope.loadingSite=false;
		});
	}

	\$scope.confirmDelete$className = function () {
		\$scope.errors.errorMessages=[];
		DAO.delete({appName: \$scope.appConfig.appName, token: \$scope.appConfig.token, instance:\$scope.${instance}, id: \$scope.${instance}.id, controller:'${instance}', action:'delete'},
		\$scope.loadingSite=true,
		function (result) {
			//\$scope.${instance}s = result;
			\$scope.flags.save = true;
			\$scope.loadingSite=false;
			window.location.href = "#/${instance}/list"
		},
		function (error) {
			\$scope.errors.showErrors = true;
			\$scope.errors.showServerError = true;
			\$scope.errors.errorMessages.push(''+error.status+' '+error.data);
			\$scope.loadingSite=false;
		});
	}
}

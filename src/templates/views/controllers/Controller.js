'use strict';
function @controller.name@(DAO, $rootScope)
{
    if ($rootScope.appConfig) {
        if (!$rootScope.appConfig.token!='') {
            window.location.href = "#/login"
        }
    }

    $rootScope.flags = {save: false};
    $rootScope.errors = {showErrors: false, showServerError: false,errorMessages:[]};

    if(!$rootScope.@class.instance@){
    $rootScope.filter = ""
    $rootScope.@class.instance@s = [];
    $rootScope.@class.instance@ = {};
    }

    $rootScope.getAll@class.name@ = function () {
        //get all
        DAO.query({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, controller: '@class.instance@', action: 'list'},
            function (result) {
                $rootScope.@class.instance@s = result;
            },
            function (error) {
            	$rootScope.errors.showErrors = true;
                $rootScope.errors.showServerError = true;
            	$rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
            });
    };

    $rootScope.new@class.name@ = function () {
    $rootScope.@class.instance@ = {};
    window.location.href = "#/@class.instance@/create"
}

    $rootScope.manualSave@class.name@ = function () {
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
        DAO.save({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, controller:'@class.instance@', action:'save'},
        function (result) {
            $rootScope.@class.instance@ = result;
            $rootScope.flags.save = true;
        },
        function (error) {
            $rootScope.flags.save = false;
            $rootScope.errors.showErrors = true;
            $rootScope.errors.showServerError = true;
            $rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
        }
)
    ;
}

$rootScope.update@class.name@ = function () {
    DAO.update({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, controller:'@class.instance@', action:'update'},
    function (result) {
    	$rootScope.@class.instance@s = result;
        $rootScope.flags.save = true;
        window.location.href = "#/@class.instance@/list"
    },
    function (error) {
        $rootScope.flags.save = false;
        $rootScope.errors.showErrors = true;
        $rootScope.errors.showServerError = true;
        $rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
    }
)
;
}

$rootScope.edit@class.name@ = function (@class.instance@){
    DAO.get({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, id: @class.instance@.id, controller:'@class.instance@', action:'show'},
function (result) {
    $rootScope.@class.instance@ = result;
    $rootScope.flags.save = true;
    window.location.href = "#/@class.instance@/edit"
}
,
function (error) {
    $rootScope.errors.showErrors = true;
    $rootScope.errors.showServerError = true;
    $rootScope.errors.errorMessages.push('Error: '+error.status+' '+error.data);
});
}

$rootScope.confirmDelete@class.name@ = function () {
    DAO.delete({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, id: $rootScope.@class.instance@.id, controller:'@class.instance@', action:'delete'},
    function (result) {
    	//$rootScope.@class.instance@s = result;
    	$rootScope.flags.save = true;
        window.location.href = "#/@class.instance@/list"
    },
    function (error) {
        $rootScope.errors.showErrors = true;
        $rootScope.errors.showServerError = true;
        $rootScope.errors.errorMessages.push(''+error.status+' '+error.data);
    }
);}
}
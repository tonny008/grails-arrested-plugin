'use strict';
function @controller.name@(DAO, $rootScope)
{
    if ($rootScope.appConfig) {
        if (!$rootScope.appConfig.token!='') {
            window.location.href = "#/login"
        }
    }

    $rootScope.flags = {save: false};
    $rootScope.errors = {showErrors: false, showServerError: false,errorMessages:''};

    if(!$rootScope.@class.instance@){
    $rootScope.filter = ""
    $rootScope.@class.instance@s = [];
    $rootScope.@class.instance@ = {};
    }

    $rootScope.getAll@class.name@ = function () {
    	$rootScope.errors.errorMessages = [];
        //get all
        DAO.query({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, controller: '@class.instance@', action: 'list'},
            function (result) {
                $rootScope.@class.instance@s = result;
            },
            function (error) {
            	$rootScope.errors.showErrors = true;
                $rootScope.errors.showServerError = true;
            	$rootScope.errors.errorMessages.push('Error:  '+error.status+' '+error.data);
            });
    };

    $rootScope.new@class.name@ = function () {
    $rootScope.@class.instance@ = {};
    window.location.href = "#/@class.instance@/create"
}

    $rootScope.manualSave@class.name@ = function () {
    	$rootScope.errors.errorMessages = [];
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
    	$rootScope.errors.errorMessages = [];
        DAO.save({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, controller:'@class.instance@', action:'save'},
        function (result) {
            $rootScope.@class.instance@ = result;
            $rootScope.flags.save = true;
        },
        function (error) {
            $rootScope.flags.save = false;
            $rootScope.errors.showErrors = true;
            $rootScope.errors.showServerError = true;
            $rootScope.errors.errorMessages.push('Error:  '+error.status+' '+error.data);
        }
)
    ;
}

$rootScope.update@class.name@ = function () {
	$rootScope.errors.errorMessages = [];
    DAO.update({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, instance:$rootScope.@class.instance@, controller:'@class.instance@', action:'update'},
    function (result) {
        $rootScope.flags.save = true;
        //if (result.status == "200") {
        //    window.location.href = "#/@class.instance@/list"
       // }
    },
    function (error) {
        $rootScope.flags.save = false;
        $rootScope.errors.showErrors = true;
        $rootScope.errors.showServerError = true;
       // $rootScope.errors.errorMessages.push('Error:  '+error.status+' '+error.data);
        window.location.href = "#/@class.instance@/list"
    }
)
;
}

$rootScope.edit@class.name@ = function (@class.instance@){
	$rootScope.errors.errorMessages = [];
    DAO.get({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, id: @class.instance@.id, controller:'@class.instance@', action:'show'},
function (result) {
    $rootScope.@class.instance@ = result;
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
	$rootScope.errors.errorMessages = [];
    DAO.delete({appName: $rootScope.appConfig.appName, token: $rootScope.appConfig.token, id: $rootScope.@class.instance@.id, controller:'@class.instance@', action:'delete'},
    function (result) {
        if (result.response == "@class.name@_deleted") {
    	//if (result.status == "200") {
            window.location.href = "#/@class.instance@/list"
        }
    },
    function (error) {
        $rootScope.errors.showErrors = true;
        $rootScope.errors.showServerError = true;
    }
);}
}
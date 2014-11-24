// Password matching directive
${shortname}.directive('passwordMatch', [function () {
	return {
	restrict: 'A',
	scope:true,
	require: 'ngModel',
	link: function (scope, elem , attrs,control) {
		var checker = function () {
		//get the value of the first password
		var e1 = scope.\$eval(attrs.ngModel);
		//get the value of the other password
		var e2 = scope.\$eval(attrs.passwordMatch);
		return e1 == e2;
	};
	scope.\$watch(checker, function (n) {
		//set the form control to valid if both
		//passwords are the same, else invalid
		control.\$setValidity('unique', n);
	});
	}
	};
}]);

//uniqueUsername directive
${shortname}.directive('uniqueUsername', ["\$http", function(\$http){
	return{
    require: 'ngModel',
		link: function (scope, element, attrs, ctrl) {
        	element.bind('blur', function (e) {
           	 if (!ctrl || !element.val()) return;
            	var currentValue = element.val();
				\$http.put('auth/lookup', {username: currentValue}).success(function (res) {
					ctrl.\$setValidity('uniquser', true);
				}).error(function (res) {
					ctrl.\$setValidity('uniquser', false);
				});
			});
		}	
	};
}]);

${shortname}.directive('loadingContainer', function () {
 return {
	 restrict: 'A',
	 scope: false,
	 link: function(scope, element, attrs) {
		 var loadingLayer = angular.element('<div class="loading"></div>');
		 element.append(loadingLayer);
		 element.addClass('loading-container');
		 scope.\$watch(attrs.loadingContainer, function(value) {
			 loadingLayer.toggleClass('ng-hide', !value);
		 });
	 }
 };
});

/*
 * Creating a service alternative method 
 *${shortname}.factory('LangService', function(\$q, \$http) {
	var service = {
	 getLang: function() {
		var d = \$q.defer();
		\$http.get('auth/userLocation')
		.success(function(data, status) {
			d.resolve(data);
		}).error(function(data, status) {
			d.reject(data);
		});
		return d.promise;
      },
      getLang2: function() {
    	  var d = \$q.defer();
    	  \$http.post('auth/userLocation', {})
    	  .success(function(data, status) {
    		  d.resolve(data);
    	  }).error(function(data, status) {
    		  d.reject(data);
    	  });
    	  return d.promise;
      }
   }
   return service;
 });
 */
<nav id="Navbar" class="navbar navbar-fixed-top navbar-inverse" role="navigation" data-ng-show="appConfig.token!=''">
		<div class="container-fluid" data-ng-controller="UserCtrl" >
	
	    <div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
        		<span class="sr-only">Toggle navigation</span>
        		<span class="icon-bar"></span>
	           	<span class="icon-bar"></span>
	           	<span class="icon-bar"></span>
			</button>
			<div class="define-spinner">
				<div ng-show="loadingSite"><i class="fa fa-spinner fa-2x fa-spin fa-inverse"></i></div>
			</div>
			<a class="homeLogo" href="#/"><i class="fa fa-home fa-2x icon-color fa-inverse"></i></a>
		</div>
		<div class="collapse navbar-collapse navbar-ex1-collapse" role="navigation">
    	<ul class="nav navbar-nav">
			<li class="controller">
				<div id="h2Header"><g:message code="default.welcome.title" args="[meta(name:'app.name')]"/></div>
			</li>
         </ul>
         <ul  class="nav navbar-nav navbar-right" >
         	<li  class="dropdown controller">
				<a class="dropdown-toggle" role="button" data-toggle="dropdown">
					<span id="userMessage">
						<span class="glyphicon glyphicon-user"></span>
							<g:message code="default.user.label" default="{{user.username}}" />
						</span>
					 	<b class="caret"></b>
				</a>
				<ul class="dropdown-menu" role="menu">
					<li>
						<a class="fa fa-gear icon-color" onclick='window.location.href="#/updateusername"' title="\${message(code: 'default.username.update', default: 'Update Username')}">
							<g:message code="default.username.update"  default="Update Username"/>	
                        </a>
					</li>

					<li>
						<a class="fa fa-gear icon-color" onclick='window.location.href="#/updatepassword"' title="\${message(code: 'default.password.update', default: 'Update password')}">
							<g:message code="default.password.update"  default="Update Password"/>	
                        </a>
					</li>
				</ul>
			</li>
			<li class="controller">
				<a data-ng-controller='UserCtrl' data-ng-click='logout()' title="\${message(code: 'security.signoff.label', default: 'Log out')}">
					<span class="glyphicon glyphicon-log-out"></span> <g:message code="security.signoff.label" default="Sign Off"/>
				</a>
			</li>
			
		</ul>
		</div>
	</div>
</nav>
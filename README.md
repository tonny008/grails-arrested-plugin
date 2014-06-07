grails-arrested-plugin
======================

The Arrested plugin is a framework that generates RESTful controllers for your GORM objects and maps them in your UrlMappings, generates AngularJS based views in the form a single page per domain entity, and finally it provides a simple token based security model.

AngularJs + RESTful = Arrested


# Installation:

Add plugin Dependency in BuildConfig.groovy :
>compile ":arrested:1.29"

## Installation information warning

We recommend you create a new project to install this plugin into, if you have an existing project be warning files such as index.gsp main.gsp i18n messages.properties messages_de.properties are overwritten. Lots of new files are added to this project.Please ensure you have backed up the project if it is an existing one.


# Getting Started

You can watch this video to help understand what you are about to do : [arrested website video](https://www.youtube.com/watch?v=popG4gucZ0Y)

For screen shots check out [arrested website screenshots](https://github.com/vahidhedayati/testingarrested/wiki)

We need to set up a couple things before we get started generating your REST controllers and Angular views.  Namely we need our User and Token Entities, a Security Filter that validates a token header field, and some base Angular js files that act as a DAO to your rest services.

To get started with arrested run:

> grails create-arrested-app


# domainClass 
To generate a REST domainClass run: 
(This will generate a Controller and put the appropriate configurations in UrlMapping to be RESTful)
> grails create-domain-class DomainClassName

Once you have generated a domainClass ensure you add at least one element inside it before proceeding to the next step:


# From 1.12 controller/view generation done in one command
> grails arrested DomainClassName



# Controller (pre 1.12) 

To generate a REST controller run: 
(This will generate a Controller and put the appropriate configurations in UrlMapping to be RESTful)

> grails create-arrested-controller DomainClassName

# View (pre 1.12)
To generate views for your newly created REST controller run: 
(This will generate a index.gsp file in your views directory ie. /views/domainclassname/index.gsp; and the javascript files to interact with your REST controller.  It's configured to use the security token and will pass a token on each request for data.)

> grails create-arrested-view DomainClassName




# How to Integrate

### Creating a user at startup:



BootStrap:
```groovy
import org.apache.shiro.crypto.hash.Sha256Hash

import arrested.ArrestedToken
import arrested.ArrestedUser

class BootStrap {

   def init = { servletContext ->
	ArrestedUser user
		ArrestedToken token
		
		user = new ArrestedUser(
			username: "admin",
			passwordHash: new Sha256Hash("admin").toHex(),
			dateCreated: new Date()
		).save()
		
		 //Create tokens for users
		token = new ArrestedToken(
			token: 'token',
			valid: true,
			owner: user.id
		).save(flush: true)
		user.setToken(token.id)
		user.save()
		
    
    }
    def destroy = {
    }
}
```



Configuration completed

For a sample site please visit [testingarrested](https://github.com/vahidhedayati/testingarrested), this project consists of a basic Books,Authors data modelling where 
Authors have many books. To log in 
>username:  admin
>password:  admin

Refer to Bootstrap configuration on this demo project to see how that works, once in, you can create authors then add books to those authors.
  

## ver 1.28+ i18n information

By default messages.properties messages_de.properties are copied to your i18n folder.


You are required to configure in Config.groovy the following configuration for any additional language you wish to support: 

```

arrested.supported.i18n=['en','de','es_PE','es']

// For all locales simply enable this:
arrested.supported.i18n=['*']
```

Please refer to [supported i18n files](https://github.com/vahidhedayati/testingarrested/wiki/Supported-i18n-locales---for-you-to-create) . These values can be used in the above array  - or use the * which are all of the supported Locales.



[For alternative method of loading up locales i.e. via a select box](https://github.com/vahidhedayati/testingarrested/wiki/Alternative-method-of-calling-locales---via-select-box)
 
[manually creating locales entries](https://github.com/vahidhedayati/testingarrested/wiki/Manually-defining-locales)

### Version info:

```
1.29 :	Bug found with getAll$domainClass, required for select boxes produced when there is dependencies.
		renamed all $rootScope calls within created Ctrl files for domainClasses to $scope. 
		
1.28 :	Further tidy up of arrestedInstaller, removed duplicate css/js files and used one set, 
		css parsed for resources. proper details on how to use i18n now provided, the files copied to 
		user's project folder. override function provided to add additional i18n support from users
		Config.groovy. Moved clock over to clockCtrl.js
		
1.27 : 	Minor bug css issues with pre 2.4 now fixed. (images still not appearing on grails pre 2.4)
 
1.26 :	Changed call method to change lang support to login/signup pages. Removes duplicate auth
		until token updates put in place.
		
1.25 : 	Live clock/user date/time displayed, i18n support passed through to grails. Further work is required 
		to pass token to header in order to stop re-authentication.
		
1.24 : 	Bug fix: Search/filter issue found fixed

1.23 :	Fixed missing fonts issue with pre 2.4 release using internal css/fonts. 

1.22 : 	proper version checks put in place nice methods to call resources/assets in place ready for future changes.
		css/js files moved internal to user application, cors plugin removed, additional fonts added to site and 
		whilst everything looks great on 2.4+, on 2.3X- the fonts no longer load up so it appears as missing icons,
		the fix currently is for those pre 2.4 to search replace ../fonts and replace with fonts in web-app/css/
		font-awesome and I think it was bootstrap-glyhphicon.css.
		 
		
1.21 : 	assets support has now been added, 2.4.0+ apps configured for assets  2.3X < support for resources remains
 
1.20 : 	various updates to support grails 2.4.0+, at the moment still relying on resources plugin. Updates to 
		edit.html to bring in line with 2.4 verification, further updates to internal script to support hibernate4
		when calling cp.

1.19 : 	ngTable added, pagination support. (issues with filtering). UI table changes due to module addition. 
		arrestedInternal Script updated:  moved html content to files and tidied up installation script.

1,18 : 	Issues with JSON parsing fixed, hopefully for good now. directive to check existing username added.
		 - and verification added to update/signup features.

1.17 :	Controller.gsp generated based upon existing Ctrl.js files. UI tidyup - update returned to row click.

1.16 : 	Introduction of search, new css styles, tidy up of existing navigation bar.

1.15 :	ui updates, tidy up

1.14 :	user added to navbar with drop down allowing updates to account. Fixed updateResources so that only
 		available ctrl files are added to ApplicationResources.

1.13 : 	i18n support added to controller responses, server responses are now being returned to angularJS, error
 		messages returned now in line with fields.

1.12 : 	Issues reported and found in older Grails with JSON parsing, and on going sign up issues. Both now fixed.
		tidy up of userController.js

1.11 : 	Issue saving records, as String added to json.parse values of user name,password,passwordConfirm within
		ArrestedUserController

1.10 : 	Sign-up added, logout button added, i18n support added, HTML pages converted to GSP pages. 	

1.9 : 	Basic AngularJS form validation added to master edit.html - numbers min/max validation string minSize/
		maxSize/pattern validations. Form update/Save disabled until form is valid

1.8 :	Should have tested 1.7 properly - whilst it worked in one field instance, bugs with multiElement. Now fixed

1.7 :	edit.html constraints not working - now fixed, added extra sha256Password encryption to testUnit for 
		controller

1.6 : 	Fixed issue with applications that have a dash / (hyphens). This was due to var in index.js also adopting 
		dashes and breaking JavaScript, 
		Added extra autoComplete="off" to login form, a limitation in where browser saved passwords auto complete 	
		does not work well with this technology.
		In order to successfully log in, user must fill in user name and password. The extra additions to 
		login.html appears to have fixed on FireFox. 

1.5 : 	changed static/Views to Views - this caused issued on 2.3.7+, removed serverHost change
		resource to a dynamic call within service.js
		
1.4 : 	Minor changes to edit.html renderManyToOne : property.type.name substring lastIndexOf . to end 
		(this may be needed in other calls, further tests needed)
		
1.3 : 	Changes made to ArrestedUser & Token so they default to arrested package - this now fixes unit tests 
		for newly created controllers, added serverURL configuration override.
		
1.2 : 	Fixed some minor issues with upper case Scripts to scripts etc.
``` 


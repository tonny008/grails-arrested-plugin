grails-arrested-plugin
======================

The Arrested plugin is a framework that generates RESTful controllers for your GORM objects and maps them in your UrlMappings, generates AngularJS based views in the form a single page per domain entity, and finally it provides a simple token based security model.

AngularJs + RESTful = Arrested


# Installation:

Add plugin Dependency in BuildConfig.groovy :
>compile ":arrested:1.18"


# Getting Started

You can watch this video to help understand what you are about to do : [arrested website video](https://www.youtube.com/watch?v=popG4gucZ0Y)


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



# Security


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
  


#### Limitations:
Nothing noticed / reported at the moment - 


### Version info:

```
1,18 ; 	Issues with JSON parsing fixed, hopefully for good now. directive to check existing username added - and verification added to update/signup features.

1.17 :	Controller.gsp generated based upon existing Ctrl.js files. UI tidyup - update returned to row click.

1.16 : 	Introduction of search, new css styles, tidy up of existing navigation bar.

1.15 :	ui updates, tidy up

1.14 :	user added to navbar with drop down allowing updates to account. Fixed updateResources so that only available ctrl files are added to ApplicationResources.

1.13 : 	il8n support added to controller responses, server responses are now being returned to angularJS, error messages returned now in line with fields.

1.12 : 	Issues reported and found in older Grails with JSON parsing, and on going sign up issues. Both now fixed. tidy up of userController.js

1.11 : 	Issue saving records, as String added to json.parse values of user name,password,passwordConfirm within ArrestedUserController

1.10 : 	Sign-up added, logout button added, il8n support added, HTML pages converted to GSP pages. 	

1.9 : 	Basic AngularJS form validation added to master edit.html - numbers min/max validation string minSize/maxSize/pattern validations. Form update/Save disabled until form is valid

1.8 :	Should have tested 1.7 properly - whilst it worked in one field instance, bugs with multiElement. Now fixed

1.7 :	edit.html constraints not working - now fixed, added extra sha256Password encryption to testUnit for controller

1.6 : 	Fixed issue with applications that have a dash / (hyphens). This was due to var in index.js also adopting dashes and breaking JavaScript, 
		Added extra autoComplete="off" to login form, a limitation in where browser saved passwords auto complete does not work well with this technology.
		In order to successfully log in, user must fill in user name and password. The extra additions to login.html appears to have fixed on FireFox. 

1.5 : 	changed static/Views to Views - this caused issued on 2.3.7+, removed serverHost change
		resource to a dynamic call within service.js
		
1.4 : 	Minor changes to edit.html renderManyToOne : property.type.name substring lastIndexOf . to end 
		(this may be needed in other calls, further tests needed)
		
1.3 : 	Changes made to ArrestedUser & Token so they default to arrested package - this now fixes unit tests 
		for newly created controllers, added serverURL configuration override.
		
1.2 : 	Fixed some minor issues with upper case Scripts to scripts etc.
``` 


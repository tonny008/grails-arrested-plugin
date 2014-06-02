@package.line@
import grails.converters.JSON
import grails.converters.XML
import arrested.ArrestedController
import org.apache.shiro.crypto.hash.Sha256Hash

class AuthController extends ArrestedController {
	
    static allowedMethods = [login: "POST", logout: "GET"]
	
	def showUpdatePassword() {
		withFormat {
			html {
				render(view: "update")
			}
		}
	}
	
	def listLocale() {
		def countries = [] 
		def supported=grailsApplication.config.arrested.supported.i18n ?: ['en','de'] 
		def locale = Locale.getAvailableLocales().collect { availableLocale ->
			def countryMap = [:]
			//def lang=availableLocale?.getLanguage()?.toString() ?: availableLocale.toString()
			def countryname=availableLocale?.getDisplayName()?.toString() ?: availableLocale.toString()
			if (supported.find{((it==availableLocale as String)||(it=='*'))}) {
			countryMap.put('value', availableLocale as String)
			countryMap.put('text', countryname)
			countries?.add(countryMap)
			}
		}
		countries = countries.sort { it }
		withFormat{
			xml {
				render countries as XML
			}
			json {
				render countries as JSON
			}
		}
	}
	
	def setLang() {
		def data=params.instance ?: request?.JSON?.instance ?: JSON.parse(params?.instance)
		String lang=data as String
		//session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'] = new Locale(org.springframework.web.servlet.support.RequestContextUtils.getLocale(request).toString().substring(0,2), lang)
		session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'] = new Locale(lang)
		renderSuccess(lang,"${message(code: 'default.lang.changed.label', default: 'Language changed')}")
	}
	
	
	def showUpdated() {
		renderSuccess("","${message(code: 'default.details.updated.label', default: 'Information has been updated')}")
	}
	
	def showUpdateUsername() {
		withFormat {
			html {
				render(view: "update-username")
			}
		}
	}
	
	def showLogin() {
		withFormat {
			html {
				render(view: "login")
			}
		}
	}
	
	def showSignup() {
		withFormat {
			html {
				render(view: "signup")
			}
		}
	}
	
	def lookup(){
		def data=request.JSON
		if (!data) {
			data=JSON.parse(params)
		}
		String username=data.username as String
		if(username){
			if (ArrestedUser.findByUsername(username)) {
				renderConflict("${message(code: 'default.username.used.label', default: 'Username already in use')}")
			} else {
				renderSuccess("0","${message(code: 'default.username.available.label', default: 'Username available')}")
			}
		} else {
			renderMissingParam("${message(code: 'default.username.missing.label', default: 'Username missing')}")
		}
	}
		
    def login(String username, String passwordHash){
        if(username){
            if(passwordHash){
                ArrestedUser user = ArrestedUser.findByUsername(username)
                if(user){
                    if (user.passwordHash == new Sha256Hash(passwordHash as String).toHex()){
                        Date valid = new Date()
                        valid + 1
                        ArrestedToken token = ArrestedToken.get(user.token)
                        if(!token){
                            user.setToken(new ArrestedToken( token: UUID.randomUUID().toString(), valid: true, owner: user.id).save(flush: true).id)
                            user.save(flush: true)
                        }else if(token.lastUpdated.time > valid.time || !token.valid){
                            token.token = UUID.randomUUID()
                            token.valid = true
                            token.save(flush: true)
                        }
                        withFormat{
                            xml {
                                render user.toObject() as XML
                            }
                            json  {
                                render user.toObject() as JSON
                            }
                        }
                    }
                    else{
                        renderConflict("${message(code: 'default.usernamepassword.invalid.label', default: 'Username and/or password incorrect')}")
                    }
                }
                else{
                   renderConflict("${message(code: 'default.usernamepassword.invalid.label', default: 'Username and/or password incorrect')}")
                }
            }
            else{
                renderMissingParam("${message(code: 'default.password.missing.label', default: 'PasswordHash missing')}")
            }
        }
        else{
            renderMissingParam("${message(code: 'default.username.missing.label', default: 'Username missing')}")
        }
    }

    def logout(String token) {
        if(token){
            ArrestedToken arrestedToken = ArrestedToken.findByToken(token)
            if(arrestedToken){
                ArrestedUser user = ArrestedUser.findByToken(arrestedToken.id)
                if(user){
                    arrestedToken.valid = false
                    arrestedToken.save(flush: true)
                    withFormat{
                        xml {
                            render "${message(code: 'default.logout.success.label', default: 'Logout successfully')}"
                        }
                        json  {
                            render "${message(code: 'default.logout.success.label', default: 'Logout successfully')}"
                        }
                    }
                }
                else{
                    renderNotFound("", "${message(code: 'default.user.notfound.label', default: 'User not found')}")
                }
            }
            else{
                renderNotFound("", "${message(code: 'default.token.notfound.label', default: 'Token not found')}")
            }
        }
        else{
            renderMissingParam("${message(code: 'default.token.missing.label', default: 'Token missing')}")
        }
    }
}

@package.line@
import grails.converters.JSON
import grails.converters.XML
import arrested.ArrestedController
import org.apache.shiro.crypto.SecureRandomNumberGenerator
import org.apache.shiro.crypto.hash.Sha512Hash
import arrested.ArrestedRole


class ArrestedUserController extends ArrestedController {

    static allowedMethods = [show: "GET", list: "GET", save: "POST", update: "PUT", delete: "DELETE"]

    def show(String token) {
        if(token){
            ArrestedToken arrestedToken = ArrestedToken.findByToken(token)
            if(arrestedToken){
                ArrestedUser user = ArrestedUser.findByToken(arrestedToken.id)
                if(user){
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

    def list() {
        def users = ArrestedUser.list().collect { it.showInformation() }
        withFormat{
            xml {
                render users as XML
            }
            json  {
                render users as JSON
            }
        }
    }

	def save(){
		String username
		String passwordHash
		String passwordConfirm
		
		def data=request.JSON
		/*if (!data) {
			data=JSON.parse(params)
		}*/
		
		if (data) {
			username=data.username as String
			passwordHash=data.passwordHash as String
			passwordConfirm=data.passwordConfirm as String
		}else{
			username=params.username
			passwordHash=params.passwordHash
			passwordConfirm=params.passwordConfirm
		}
		
		if(username){
			if((passwordHash&&passwordConfirm)&&(passwordHash.equals(passwordConfirm))){
				if (ArrestedUser.findByUsername(username)) {
					renderConflict("${message(code: 'default.username.used.label', default: 'Username already in use')}")
				} else {
				
				
					def definedRole=config.role ?: 'Administrator'
					def userRole = ArrestedRole.findByName(definedRole)
					if (!userRole) {
						userRole = new ArrestedRole(name: definedRole)
						userRole.addToPermissions("*:*")
						userRole.save()
					}	
				
					def passwordSalt = new SecureRandomNumberGenerator().nextBytes().getBytes()
					def user = new ArrestedUser( username:"admin", passwordHash: new Sha512Hash("password", passwordSalt,1024).toBase64(), passwordSalt:passwordSalt, dateCreated:new Date())
				
					user.addToRoles(userRole)
					user.save(flush:true, failOnError:true)
				
				
					//ArrestedUser user = new ArrestedUser( username:username, passwordHash: new Sha256Hash(passwordHash).toHex() )
					//user.save(flush: true)

					ArrestedToken token = new ArrestedToken( token: 'token', valid: true, owner: user.id )
					token.save(flush: true)
					user.setToken(token.id)
					if(user.save(flush: true)){
						withFormat {
							xml {
								response.status = 200
								render user.toObject() as XML
							}
							json {
								response.status = 200
								render user.toObject() as JSON
							}
						}
					}else{
						render409orEdit(user)
					}
				}

			}else{
				renderMissingParam("${message(code: 'default.password.missing.label', default: 'PasswordHash missing')}")
			}
		}else{
			renderMissingParam("${message(code: 'default.username.missing.label', default: 'Username missing')}")
		}
	}
	
	def updateUsername(String token) {
		def instance=request.JSON.instance
		if (!instance) {
			instance=JSON.parse(params.instance)
		}
		if(instance.username){
			if(token){
				ArrestedToken arrestedToken = ArrestedToken.findByToken(token)
				if(arrestedToken){
					ArrestedUser user = ArrestedUser.findByToken(arrestedToken.id)
					if(user){
						if (user.username != instance.username && ArrestedUser.findByUsername(instance.username as String)){
							renderConflict("${message(code: 'default.username.used.label', default: 'Username already in use')}")
						
						} else if (user.username == instance.username){
							renderConflict("${message(code: 'default.own.username.label', default: 'Can not set username to existing username')}")
						} else {
							user.properties = instance
							if(user.save(flush: true)){
								withFormat {
									xml {
										response.status = 200
										render user.toObject() as XML
									}
									json {
										response.status = 200
										render user.toObject() as JSON
									}
								}
							}
							else{
								render409orEdit(user)
							}
						}
					}
					else{
						renderNotFound(data.id, "${message(code: 'default.user.notfound.label', default: 'User not found')}")
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
		else{
			renderMissingParam("${message(code: 'default.username.missing.label', default: 'Username missing')}")
		}
	}
	
	def updatePassword(String token) {
		def instance=request.JSON.instance
		if (!instance) {
			instance=JSON.parse(params.instance)
		}
		if(instance.passwordHash){
			if(token){
				ArrestedToken arrestedToken = ArrestedToken.findByToken(token)
				if(arrestedToken){
					ArrestedUser user = ArrestedUser.findByToken(arrestedToken.id)
					
					if(user){
							instance.username=user.username
							def passwordSalt = new SecureRandomNumberGenerator().nextBytes().getBytes()
							//instance.passwordHash=new Sha256Hash(instance.passwordHash).toHex()
							instance.passwordHash = new Sha512Hash("password", passwordSalt,1024).toBase64()
							instance.passwordSalt = passwordSalt
							user.properties = instance
							if(user.save(flush: true)){
								withFormat {
									xml {
										response.status = 200
										render user.toObject() as XML
									}
									json {
										response.status = 200
										render user.toObject() as JSON
									}
								}
							}
							else{
								render409orEdit(user)
							}
					}
					else{
						renderNotFound(data.id, "${message(code: 'default.user.notfound.label', default: 'User not found')}")
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
		else{
			renderMissingParam("${message(code: 'default.username.missing.label', default: 'Username missing')}")
		}
	}

	
    def update(String token) {
		def instance=request.JSON.instance
		if (!instance) {
			instance=JSON.parse(params.instance)
		}
        if(instance.username){
            if(token){
                ArrestedToken arrestedToken = ArrestedToken.findByToken(token)
                if(arrestedToken){
                    ArrestedUser user = ArrestedUser.findByToken(arrestedToken.id)
                    if(user){
                        if (user.username != instance.username && ArrestedUser.findByUsername(instance.username as String)){
                            renderConflict("${message(code: 'default.username.used.label', default: 'Username already in use')}")
                        } else {
							//instance.passwordHash=new Sha256Hash(instance.passwordHash).toHex()
							def passwordSalt = new SecureRandomNumberGenerator().nextBytes().getBytes()
							//instance.passwordHash=new Sha256Hash(instance.passwordHash).toHex()
							instance.passwordHash = new Sha512Hash("password", passwordSalt,1024).toBase64()
							instance.passwordSalt = passwordSalt
                            user.properties = instance
                            if(user.save(flush: true)){
                                withFormat {
                                    xml {
                                        response.status = 200
                                        render user.toObject() as XML
                                    }
                                    json {
                                        response.status = 200
                                        render user.toObject() as JSON
                                    }
                                }
                            }
                            else{
                                render409orEdit(user)
                            }
                        }
                    }
                    else{
                        renderNotFound(data.id, "${message(code: 'default.user.notfound.label', default: 'User not found')}")
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
        else{
            renderMissingParam("${message(code: 'default.username.missing.label', default: 'Username missing')}")
        }
    }

    def delete(String token) {
        if(token){
            ArrestedToken arrestedToken = ArrestedToken.findByToken(token)
            if (arrestedToken){
                ArrestedUser user = ArrestedUser.findByToken(arrestedToken.id)
                if (user){
                    arrestedToken.delete(flush: true)
                    user.delete(flush: true)
                    withFormat {
                        xml {
                            response.status = 200
                            render "${message(code: 'default.username.deleted.label', default: 'User deleted')}"
                        }
                        json {
                            response.status = 200
                            render "${message(code: 'default.username.deleted.label', default: 'User deleted')}"
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
	
	def getConfig() {
		grailsApplication.config.arrested
	}
}
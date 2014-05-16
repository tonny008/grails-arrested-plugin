class BootStrap {
	def init = { servletContext ->
		def (adminRole,  guestRole) = setUpRoles()
		ArrestedUser admin
		ArrestedToken token
		def admin = ArrestedUser.findByUsername("admin")
		if (!admin) { 
		admin = new ArrestedUser(
			username: "admin",
			passwordHash: new Sha256Hash("password").toHex(),
			dateCreated: new Date()
		).save()
		
		 //Create tokens for users
		token = new ArrestedToken(
			token: 'token',
			valid: true,
			owner: user.id
		).save(flush: true)
		admin.setToken(token.id)
		admin.save()
		if (!admin.roles) {
			admin.addToRoles(adminRole)
			.addToRoles(guestRole)
			.save(flush:true, failOnError: true)
		}
	}

	}
	def destroy = {
	}

	private setUpRoles() {
		def admin = ArrestedRole.findByName(Role.ADMIN) ?: new Role(name: Role.ADMIN).save(failOnError: true)
		safelyAddPermission admin, "*"

		def guest = ArrestedRole.findByName(Role.GUEST) ?: new Role(name: Role.GUEST).save(failOnError: true)

		return [admin, guest]
	}

	private safelyAddPermission(entity, String permission) {
		if (!entity.permissions?.contains(permission)) {
			entity.addToPermissions permission
		}
	}
}
@package.line@

class ArrestedRole {
	
	static final ADMIN = "Admin"
	static final GUEST = "Guest"
	
    String name
	static hasMany = [ users: ArrestedUser, permissions: String ]
	static belongsTo = ArrestedUser
    static constraints = {
        name blank: false, unique: true, nullable:false
    }
	
	String toString() {
		name
	}

	boolean equals(Object o) {
		if(o instanceof ArrestedRole) {
			return this.name == o.name
		}
		return false
	}


}
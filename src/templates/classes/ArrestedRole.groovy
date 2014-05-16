@package.line@

class ArrestedRole {
	
	static final ADMIN = "Admin"
	static final GUEST = "Guest"
	
    String name
	static hasMany= [ permissions: String]
    static constraints = {
        name blank: false, unique: true
    }
	
	String toString() {
		name
	}

	boolean equals(Object o) {
		if(o instanceof Role) {
			return this.name == o.name
		}
		return false
	}


}
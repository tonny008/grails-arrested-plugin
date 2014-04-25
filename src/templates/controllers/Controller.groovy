@package.line@<% import grails.persistence.Event %>import grails.converters.JSON
import grails.converters.XML
import arrested.ArrestedController

class @controller.name@ extends ArrestedController {

    static allowedMethods = [show: "GET", list: "GET", save: "POST", update: "PUT", delete: "DELETE"]
	def listing() { 
		withFormat {
			html {
				render(view: "list")
			}
		}
	}
	def edit() {}
	
    def show(Long id) {
        if(id){
            @class.name@ instance = @class.name@.get(id)
            if(instance){
                withFormat{
                    xml {
                        render instance as XML
                    }
                    json {
                        render instance as JSON
                    }
                }
            }
            else{
				renderNotFound(id, "\${message(code: 'default.@class.name@.notfound.label', default:'@class.name@ not found')}")
				
            }
        }
        else{
            renderMissingParam("\${message(code: 'default.id.missing.label', default: 'id missing')}")
        }
    }

    def list() {
        def instances = @class.name@.list()
        withFormat{
            xml {
                render instances as XML
            }
            json {
                render instances as JSON
            }
        }
    }

    def save() {
		if (request.JSON.instance) {
			def data = request.JSON.instance
			<%if(!cp){%>@class.name@ instance = new @class.name@(data)<%}else{%>@class.name@ instance = new @class.name@() <%  excludedProps = Event.allEvents.toList() << 'id' << 'version'
				allowedNames = domainClass.name << 'dateCreated' << 'lastUpdated'
				props = domainClass.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) }
				for (p in props) {
					if (p.manyToOne || p.oneToOne){%>
						if(data.${p.name}) instance.${p.name} = ${p.type.name}.get(data.${p.name}.id as Long)
						<%}else if ((p.oneToMany && !p.bidirectional) || (p.manyToMany && p.isOwningSide())) {%>
						data.${p.name}.each{
							instance.${p.name}.add(${p.type.name}.get(it.id as Long))
						}
						<%}else{%>
						if(data.${p.name}) instance.${p.name} = data.${p.name}
						<%}}}%>

            if(instance.save(flush: true)){
                withFormat {
                    xml {
                        response.status = 200
                        render instance as XML
                    }
                    json {
                        response.status = 200
                        render instance as JSON
                    }
                }
            }
            else{
                render409orEdit(instance)
            }
        }
        else{
			renderMissingParam("\${message(code: 'default.@class.instance@.missing.label', default: '@class.instance@ missing')}")
        }
    }

    def update() {
        if (params.instance) {
            def data = JSON.parse(params.instance)
            @class.name@ instance = @class.name@.get(data.id as Long)
            if(instance){ <%if(!cp){%>instance.properties = data
                    <%}else{
                    excludedProps = Event.allEvents.toList() << 'id' << 'version'
                    allowedNames = domainClass.name << 'dateCreated' << 'lastUpdated'
                    props = domainClass.findAll { allowedNames.contains(it.name) && !excludedProps.contains(it.name) && it.type != null && !Collection.isAssignableFrom(it.type) }
                    for (p in props) {
                        if (p.manyToOne || p.oneToOne){%>
                            if(data.${p.name}) instance.${p.name} = ${p.type.name}.get(data.${p.name}.id as Long)
                            <%}else if ((p.oneToMany && !p.bidirectional) || (p.manyToMany && p.isOwningSide())) {%>
                            params.instance.${p.name}.each{
                                instance.${p.name}.add(${p.type.name}.get(it.id as Long))
                            }
                            <%}else{%>
                            if(data.${p.name}) instance.${p.name} = data.${p.name}
                            <%}}}%>if(instance.save(flush: true)){
                    withFormat {
                        xml {
                            response.status = 200
                            render instance as XML
                        }
                        json {
                            response.status = 200
                            render instance as JSON
                        }
                    }
                }
                else{
                    render409orEdit(instance)
                }
            }
            else{
				renderNotFound(data.id, "\${message(code: 'default.@class.name@.notfound.label', default: '@class.name@ not found')}")
            }
        }
        else{
			renderMissingParam("\${message(code: 'default.@class.instance@.missing.label', default: '@class.instance@ missing')}")
        }
    }

    def delete(Long id) {
        if (id){
            @class.name@ instance = @class.name@.get(id)
            if (instance){
                instance.delete(flush: true)
                withFormat {
                    xml {
                        response.status = 200
						render "\${message(code: 'default.@class.name@.deleted.label', default: '@class.name@ deleted')}"
                    }
                    json {
                        response.status = 200
						render "\${message(code: 'default.@class.name@.deleted.label', default: '@class.name@ deleted')}"
                    }
                }
            }
            else{
				renderNotFound(id, "\${message(code: 'default.@class.name@.notfound.label', default: '@class.name@ not found')}")
            }
        }
        else{
			renderMissingParam("\${message(code: 'default.id.missing.label', default: 'id missing')}")
        }
    }
}

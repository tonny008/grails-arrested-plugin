class ArrestedGrailsPlugin {
    def version = "1.8"
    def grailsVersion = "2.0 > *"
    def title = "Arrested Plugin"
    def description = 'Generates RESTful controllers for domain classes and maps them in UrlMappings, generates single-page AngularJS-based views, and provides simple token-based security'
    def documentation = "http://grails.org/plugin/arrested"
    def license = "APACHE"
    def developers = [
        [name: 'Marlon Rojas', email: 'marlon.rojas@puresrc.com'],
        [name: 'Juan Bonilla', email: 'juanjose.bonilla@puresrc.com'],
		[name: 'Vahid Hedayati', email: 'badvad@gmail.com']
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/PureSrc/grails-arrested-plugin/issues']
    def scm = [url: 'https://github.com/PureSrc/grails-arrested-plugin']
}

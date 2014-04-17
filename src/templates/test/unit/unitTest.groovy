@package.line@

import org.junit.*
import static org.junit.Assert.*
import grails.test.mixin.*
import arrested.ArrestedUser
import arrested.ArrestedToken
import grails.test.mixin.support.*

@TestFor(@controller.name@)
@Mock([ArrestedUser,ArrestedToken])

class @controller.name@UnitTest {
    ArrestedUser user
    ArrestedToken token
    @class.name@ @class.name@Test

    void setUp() {
        // Tear down logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void testGetAll(){
        controller.getAll()
        assertNotNull(response)
        assert response.json?.id.size() >= 0
    }

    void testCreate(){
        params.@class.instance@ = [] //We must type here the required attributes for the class
        controller.create()
        assert response.json?.response == "@class.name@_created"
        assertNotNull(response.json.id)
        assertNotNull(@class.name@.findById(response.json.id as Long))
    }

    void testUpdate(){
        params.@class.instance@ = [id:1]
        controller.update()
        assertEquals(response.json.response,"@class.name@_updated")
    }

    void testDelete(){
        params.id = 1
        controller.delete()
        assertEquals(response.json.response,"@class.name@_deleted")
        assertNull(@class.name@.findById(params.id as Long))
    }
}
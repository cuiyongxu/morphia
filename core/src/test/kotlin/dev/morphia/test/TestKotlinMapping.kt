package dev.morphia.test

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import org.bson.types.ObjectId
import org.testng.Assert.assertEquals
import org.testng.Assert.assertFalse
import org.testng.annotations.Test

class TestKotlinMapping : TestBase() {
    @Test
    fun dataClasses() {
        val list = datastore.mapper.map(MyClass::class.java)
        assertFalse(list.isEmpty())
        val myClass = MyClass(ObjectId(), 42)
        datastore.save(myClass)
        val loaded = datastore.find(MyClass::class.java)
            .first()

        assertEquals(loaded, myClass)
    }
}

@Entity
data class MyClass(@Id val id: ObjectId, val value: Int = 0)
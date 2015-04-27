package com.akirkpatrick.cplat

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.LinkedHashMap

/**
 * @author alfie
 */

public data class Customer(Id val id:String, val firstName:String, val lastName: String) {
    private val properties= LinkedHashMap<String, Any>()

    JsonAnyGetter public fun get(name: String): Any? {
        return properties.get(name)
    }

    JsonAnySetter public fun set(name: String, value: Any) {
        properties.put(name, value)
    }

    public val all : LinkedHashMap<String, Any> get() {
        return properties
    }
}

public trait CustomerRepository : MongoRepository<Customer, java.lang.String> {
    fun findByFirstName(firstName: String) : Customer
}


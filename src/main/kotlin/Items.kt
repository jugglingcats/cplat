package com.akirkpatrick.cplat

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Component
import java.lang
import java.util.LinkedHashMap
import java.util.UUID
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path

/**
 * @author alfie
 */

public interface ItemRepository : MongoRepository<Item, lang.String> {
}

public JsonCreator class Item(Id var id: String? = null, var title: String? = null) {
    private JsonIgnore val properties = LinkedHashMap<String, Any>()

    public JsonAnySetter fun set(name: String, value: Any) {
        properties.put(name, value)
    }

    public JsonAnyGetter val all: Map<String, Any> get() {
        return properties
    }
}

Component
Path("/item")
public open class Items {
    Autowired
    private var repository: ItemRepository? = null

    POST fun create(item: Item) {
        item.id=UUID.randomUUID().toString()
        repository!!.insert(item)
    }

    GET fun list() : List<Item> {
        return repository!!.findAll();
    }
}
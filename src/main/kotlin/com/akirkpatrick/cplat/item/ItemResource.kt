package com.akirkpatrick.cplat.item

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

Component
Path("/item")
public open class ItemResource Autowired constructor(val repository: ItemRepository) {
    POST fun create(item: Item) {
        repository.insert(item)
    }

    GET fun list() : List<Item> {
        return repository.findAll();
    }
}
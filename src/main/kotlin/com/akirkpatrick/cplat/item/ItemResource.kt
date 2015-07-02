package com.akirkpatrick.cplat.item

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam

Component
Path("/item")
public open class ItemResource Autowired constructor(val repository: ItemRepository) {
    POST fun create(item: Item) {
        repository.insert(item)
    }

    GET fun list() : List<Item> {
        return repository.findAll()
    }

    GET Path("{itemId}") fun getItem(PathParam("itemId") itemId: java.lang.String) : Item {
        return repository.findOne(itemId)
    }

}
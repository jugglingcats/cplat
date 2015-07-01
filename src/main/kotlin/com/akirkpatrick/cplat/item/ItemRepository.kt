package com.akirkpatrick.cplat.item

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author alfie
 */
public interface ItemRepository : MongoRepository<Item, java.lang.String> {
}
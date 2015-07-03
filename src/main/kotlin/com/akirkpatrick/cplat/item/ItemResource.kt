package com.akirkpatrick.cplat.item

import com.akirkpatrick.cplat.es.ElasticSearchServer
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.ws.rs.*

Component
Path("/item")
public open class ItemResource Autowired constructor(val repository: ItemRepository, val es: ElasticSearchServer) {
    POST
    Consumes("application/json")
    fun create(item: Item) {
        repository.insert(item)
    }

    GET
    Path("list")
    Produces("application/json")
    fun list(): List<Item> {
        return repository.findAll()
    }

    GET
    Path("{itemId}")
    Produces("application/json")
    fun getItem(PathParam("itemId") itemId: java.lang.String): Item {
        return repository.findOne(itemId)
    }

    GET
    Path("search")
    Produces("application/json")
    fun search(QueryParam("q") query: String): SearchResponse {
        return es.getClient().prepareSearch("itemindex")
                .setTypes("item")
                .setSearchType(SearchType.QUERY_AND_FETCH)
                .setQuery(QueryBuilders.matchAllQuery())
                .setFrom(0).setSize(60).setExplain(true)
                .execute().actionGet()
    }
}
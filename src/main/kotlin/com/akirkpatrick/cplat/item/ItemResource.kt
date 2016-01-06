package com.akirkpatrick.cplat.item

import com.akirkpatrick.cplat.account.Account
import com.akirkpatrick.cplat.server.InjectAccount
import com.fasterxml.jackson.annotation.JsonIgnore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.ws.rs.*

@Component
@Path("/item")
public open class ItemResource @Autowired constructor(val repository: ItemRepository) {
    val log = LoggerFactory.getLogger(ItemResource::class.java)

    @POST
    @Consumes("application/json")
    fun create(item: Item) {
        repository.insert(item)
    }

    @GET
    @Path("list")
    @Produces("application/json")
    fun list(): List<Item> {
        return repository.findAll()
    }

    @GET
    @Path("{itemId}")
    @Produces("application/json")
    fun getItem(@PathParam("itemId") itemId: java.lang.String): Item {
        return repository.findOne(itemId)
    }

    @POST
    @Path("{itemId}/upvote")
    fun upvoteItem(@InjectAccount account: Account, @PathParam("itemId") itemId: java.lang.String): Int {
        log.info("Upvote by user: {}", account);

        val item: Item=repository.findOne(itemId)
        item.votes++
        repository.save(item)
        return item.votes
    }

//    @GET
//    @Path("search")
//    @Produces("application/json")
//    fun search(@QueryParam("q") query: String): String {
//        val hits: Array<out SearchHit>? = es.getClient().prepareSearch("itemindex")
//                .setTypes("item")
//                .setSearchType(SearchType.DEFAULT)
//                .setQuery(QueryBuilders.queryStringQuery(query))
//                //                .setFrom(0).setSize(60).setExplain(true)
//                .execute().actionGet().getHits().hits()
//
//        val objectMapper = ObjectMapper()
//        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
//        val valueAsString = objectMapper.writeValueAsString(hits)
//        return valueAsString;
//    }

    class SearchHitMixin {
        public @JsonIgnore var sourceRef : String = ""
    }
}


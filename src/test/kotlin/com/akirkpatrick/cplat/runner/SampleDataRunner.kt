package com.akirkpatrick.cplat.runner

import com.akirkpatrick.cplat.config.ElasticSearchConfig
import com.akirkpatrick.cplat.es.ElasticSearchServer
import com.akirkpatrick.cplat.facet.FacetType
import com.akirkpatrick.cplat.facet.FacetTypeRepository
import com.akirkpatrick.cplat.item.Item
import com.akirkpatrick.cplat.item.ItemRepository
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component

/**
 * @author alfie
 */
public fun main(args: Array<String>) {
    val ctx: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()
    ctx.getEnvironment().addActiveProfile(ElasticSearchConfig.ES_ENABLED)
    ctx.scan("com.akirkpatrick.cplat")
    ctx.refresh()

    val runner = ctx.getBean(javaClass<Runner>())
    runner.run()
}

@Component class Runner @Autowired constructor(val items: ItemRepository, val facetdefs: FacetTypeRepository, val es: ElasticSearchServer) {
    fun run() {
        es.start()

        try {
            val root= FacetType("category", "Category")
            val bike= FacetType("bike", "Complete Bikes")
            val component= FacetType("component", "Components")
//            val mfr= FacetType(4, "Manufacturer", "mfr", "string")
//            val koga= FacetType(5, "Koga Miyata")
//            val year= FacetType(6, "Year", "year", "number")
//            root.related.addAll(arrayOf(bike.id))
//            bike.related.addAll(arrayOf(road.id, mfr.id, year.id))
//            mfr.related.addAll(arrayOf(koga.id))

            val item = Item("xxx", "yyy")
            item.facet.put("facet", Item.FacetRef(2, "Bike"))
            item.facet.put("bike", Item.FacetRef(3, "Road"))
            item.facet.put("mfr", Item.FacetRef(5, "Koga Miyata"))
            item.facet.put("year", Item.FacetRef(3, 1989))
            items.save(item)

            val result = es.getClient().prepareSearch("itemindex")
                    .setTypes("item")
                    .setSearchType(SearchType.DEFAULT)
                    .setQuery(QueryBuilders.queryStringQuery("mfr.value:koga"))
                    .execute().actionGet().getHits().hits()

            print("GOT RESULTS: ${result.size()}")
        } finally {
            es.stop()
        }
    }
}

package com.akirkpatrick.cplat

import com.akirkpatrick.cplat.config.ApplicationConfig
import com.akirkpatrick.cplat.config.ElasticSearchConfig
import com.akirkpatrick.cplat.item.Item
import com.akirkpatrick.cplat.item.ItemRepository
import com.akirkpatrick.cplat.server.ServerRunner
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.AnnotationConfigApplicationContext

public fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger(ApplicationConfig::class.java)

    val ctx: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()
    ctx.environment.addActiveProfile(ElasticSearchConfig.ES_ENABLED)
    ctx.scan("com.akirkpatrick.cplat")
    ctx.refresh()

    val repository=ctx.getBean(ItemRepository::class.java)

    val obj = Item("x", "Alfie Kirkpatrick")
    obj.set("age", 46)

    repository.save(obj)

    val item = repository.findOne(java.lang.String("x"))
    log.info("Customer: {}", item)
    log.info("Properties: {}", item.all())

    log.info("Starting Undertow server...");
    ctx.getBean(ServerRunner::class.java).run()
}

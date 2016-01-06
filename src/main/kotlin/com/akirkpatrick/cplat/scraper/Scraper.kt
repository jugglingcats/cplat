package com.akirkpatrick.cplat.scraper

import com.akirkpatrick.cplat.config.ApplicationConfig
import com.akirkpatrick.cplat.item.Item
import com.akirkpatrick.cplat.item.ItemRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.stereotype.Component
import kotlin.collections.drop
import kotlin.collections.forEach

val log = LoggerFactory.getLogger(javaClass<ApplicationConfig>())

public fun main(args: Array<String>) {
    val ctx: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()
    ctx.scan("com.akirkpatrick.cplat")
    ctx.refresh()

    ctx.getBean(javaClass<ScraperRunner>()).run()

    //    val doc = Jsoup.connect("http://www.lfgss.com/microcosms/548/").userAgent("").get()
    //    val title=doc.title()
    //    log.info("Title: {}", title)
    //
    //    doc.select(".cell-title a").forEach {
    //        val itemlink = it.attr("abs:href")
    //        log.info("Item: {}, link: {}", it.text(), itemlink)
    //
    //        processPostUrl(itemlink)
    //    }
    //    processPostUrl("http://www.lfgss.com/conversations/270398/")
}

@Component
public open class ScraperRunner @Autowired constructor(val repository: ItemRepository) {
    public fun run() {
        processPostUrl("http://www.lfgss.com/conversations/270398/")
    }

    fun processPostUrl(itemlink: String) {
        val itemdoc = Jsoup.connect(itemlink).userAgent("").get()

        val header = itemdoc.select(".item > .item-header")

        val title = header.select("#title").text()
        log.info("Title: {}", title)

        val date8601 = header.select("time").attr("datetime")
        log.info("Created: {}", date8601)

        val comments = itemdoc.select(".list-comments > .comment-item")
        val detail=processPost(comments.first())

        val item = Item(detail.id, title)
        item.set("date", date8601)
        item.set("author", detail.author)
        item.set("content", detail.content)

        repository.save(item)

        comments.drop(1).forEach {
            processComment(it)
        }
    }

    data class PostDetail(val id: String, val author: String, val content: String)

    fun processPost(post: Element): PostDetail {
        val author = post.select(".comment-item-header > .comment-item-author").text()
        log.info("Author: {}", author)

        val id = post.select("> a").attr("id")
        log.info("id: {}", id)

        val content = post.select(".comment-item-body").html()
        log.info("Content: {}", content)

        return PostDetail(id, author, content)
    }

    fun processComment(it: Element) {

    }
}
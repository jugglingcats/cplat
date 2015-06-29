import com.akirkpatrick.cplat.ApplicationConfig
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import processPost

val log = LoggerFactory.getLogger(javaClass<ApplicationConfig>())

public fun main(args: Array<String>) {
    val doc = Jsoup.connect("http://www.lfgss.com/microcosms/548/").userAgent("").get()
    val title=doc.title()
    log.info("Title: {}", title)

    doc.select(".cell-title a").forEach {
        val itemlink = it.attr("abs:href")
        log.info("Item: {}, link: {}", it.text(), itemlink)

        val itemdoc=Jsoup.connect(itemlink).userAgent("").get()

        val comments = itemdoc.select(".list-comments > .comment-item")
        processPost(comments.first())

        comments.drop(1).forEach {
            processComment(it)
        }
    }
}

fun processPost(post: Element) {
    val author = post.select(".comment-item-header > .comment-item-author").text()
    log.info("Author: {}", author)
}

fun processComment(it: Element) {

}

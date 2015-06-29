import com.akirkpatrick.cplat.ApplicationConfig
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

public fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger(javaClass<ApplicationConfig>())

    val doc = Jsoup.connect("http://www.lfgss.com/microcosms/548/").userAgent("").get();
    val title=doc.title();
    log.info("Title: {}", title);

    doc.select(".cell-title a").forEach {
        log.info("Item: {}", it.text());
    }
}

package com.akirkpatrick.cplat

import com.mongodb.Mongo
import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.api.core.ResourceConfig
import com.sun.jersey.spi.container.WebApplication
import com.sun.jersey.spi.container.servlet.ServletContainer
import com.sun.jersey.spi.spring.container.SpringComponentProviderFactory
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.servlet.Servlets
import io.undertow.servlet.api.InstanceFactory
import io.undertow.servlet.api.InstanceHandle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.stereotype.Component
import java.util.HashMap
import java.util.concurrent.atomic.AtomicLong
import javax.servlet.Servlet
import javax.servlet.ServletConfig
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

data public class Greeting(val id: Long, val content: String)

Component
Path("/test")
public class GreetingController {
    val counter = AtomicLong()

    GET Path("/greeting") Produces("application/json")
    public fun greeting(): Greeting {
        return Greeting(counter.incrementAndGet(), "Hello World")
    }
}

Component
public open class Test() {
    private var repository : CustomerRepository? = null

    Autowired constructor(repository: CustomerRepository) : this() {
        this.repository=repository
    }

    public fun test(): Customer {
        return repository!!.findByFirstName("Alfie");
    }
}

Configuration
EnableMongoRepositories
open public class ApplicationConfig() {
    Bean(name = arrayOf("mongoTemplate"))
    public open fun buildMongoTemplate(): MongoTemplate {
        var mongo = Mongo()
        return MongoTemplate(mongo, "mongo_test")
    }

}

Component
public open class Runner {
    fun run(ctx: ConfigurableApplicationContext) {
        val map = HashMap<String, String>()
        map.put("path.work", "es-temp")
//        ElasticSearchServer(map).start()

        val instanceFactory = JerseyServletInstanceFactory(ctx)

        val jerseyServletInfo = Servlets.servlet("JerseyServlet", javaClass<JerseyServlet>(), instanceFactory)
                .addInitParam("com.sun.jersey.api.json.POJOMappingFeature", "true")
                .addMapping("/api/*")

        val classLoader = this.javaClass.getClassLoader()
        val resourceManager = DevModeResourceManager(classLoader)
        val deploymment = Servlets.deployment()
                .setClassLoader(classLoader)
                .setContextPath("/")
                .setDeploymentName("cplat")
                .addServlet(jerseyServletInfo)
                .setResourceManager(resourceManager)

        val deploymentManager = Servlets.defaultContainer().addDeployment(deploymment)
        deploymentManager.deploy()

        var mainHandler = Handlers.path()
                .addPrefixPath("/", deploymentManager.start())

        val server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(mainHandler)
                .build()

        server.start()
    }
}

class JerseyServletInstanceFactory(val ctx: ConfigurableApplicationContext) : InstanceFactory<Servlet> {
    override fun createInstance(): InstanceHandle<Servlet>? {
        return object : InstanceHandle<Servlet> {
            override fun getInstance(): Servlet? {
                return JerseyServlet(ctx)
            }

            override fun release() {
                // TODO: H: shutdown server
            }
        }
    }
}

class JerseyServlet(val ctx: ConfigurableApplicationContext) : ServletContainer() {
    override fun initiate(rc: ResourceConfig?, wa: WebApplication) {
        wa.initiate(rc, SpringComponentProviderFactory(rc, ctx))
    }

    override fun getDefaultResourceConfig(props: MutableMap<String, Any>?, servletConfig: ServletConfig?): ResourceConfig? {
        return DefaultResourceConfig()
    }
}

public fun main(args: Array<String>) {
    val log = LoggerFactory.getLogger(javaClass<ApplicationConfig>())

    val ctx: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()
    ctx.scan("com.akirkpatrick.cplat")
    ctx.refresh()

    val repository=ctx.getBean(javaClass<CustomerRepository>())

    val obj = Customer("x", "Alfie", "Kirkpatrick")
    obj.set("age", 46)

    repository.save(obj)

    val customer = repository.findByFirstName("Alfie")
    log.info("Customer: {}", customer)
    log.info("Properties: {}", customer.all)

    log.info("Starting Undertow server...");
    ctx.getBean(javaClass<Runner>()).run(ctx)
}

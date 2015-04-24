package com.akirkpatrick.cplat

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
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
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

Configuration
open public class Application {
    fun run(ctx: ConfigurableApplicationContext) {
        val map = HashMap<String, String>()
        map.put("path.work", "es-temp")
        ElasticSearchServer(map).start()

        val instanceFactory = JerseyServletInstanceFactory(ctx)

        val info = Servlets.servlet("JerseyServlet", javaClass<JerseyServlet>(), instanceFactory)
                .addInitParam("com.sun.jersey.api.json.POJOMappingFeature", "true")
                .addMapping("/*")

        val deploymentInfo = Servlets.deployment()
                .setClassLoader(this.javaClass.getClassLoader())
                .setContextPath("/")
                .setDeploymentName("cplat")
                .addServlet(info)

        val deploymentManager = Servlets.defaultContainer().addDeployment(deploymentInfo)
        deploymentManager.deploy()

        var handler = Handlers.path()
                .addPrefixPath("/", deploymentManager.start())

        val server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(handler)
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
    System.out.println("Here...")

    val ctx: AnnotationConfigApplicationContext = AnnotationConfigApplicationContext()
    ctx.scan("com.akirkpatrick.cplat")
    ctx.refresh()

    System.out.println(ctx.getBean(GreetingController().javaClass))

    ctx.getBean(javaClass<Application>()).run(ctx)
}
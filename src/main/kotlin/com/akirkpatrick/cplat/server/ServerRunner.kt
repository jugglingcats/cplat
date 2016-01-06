package com.akirkpatrick.cplat.server

import com.akirkpatrick.cplat.config.ElasticSearchConfig
import com.akirkpatrick.cplat.es.ElasticSearchServer
import com.akirkpatrick.cplat.support.DevModeResourceManager
import io.undertow.Handlers
import io.undertow.Undertow
import io.undertow.servlet.Servlets
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(ElasticSearchConfig.ES_ENABLED)
public open class ServerRunner @Autowired constructor(val ctx: ConfigurableApplicationContext, val es: ElasticSearchServer) {
    fun run() {
        es.start()

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
package com.akirkpatrick.cplat.server

import com.sun.jersey.api.core.DefaultResourceConfig
import com.sun.jersey.api.core.ResourceConfig
import com.sun.jersey.spi.container.WebApplication
import com.sun.jersey.spi.container.servlet.ServletContainer
import com.sun.jersey.spi.spring.container.SpringComponentProviderFactory
import org.springframework.context.ConfigurableApplicationContext
import javax.servlet.ServletConfig

class JerseyServlet(val ctx: ConfigurableApplicationContext) : ServletContainer() {
    override fun initiate(rc: ResourceConfig?, wa: WebApplication) {
        wa.initiate(rc, SpringComponentProviderFactory(rc, ctx))
    }

    override fun getDefaultResourceConfig(props: MutableMap<String, Any>?, servletConfig: ServletConfig?): ResourceConfig? {
        return DefaultResourceConfig()
    }
}
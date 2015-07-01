package com.akirkpatrick.cplat.server

import com.akirkpatrick.cplat.server.JerseyServlet
import io.undertow.servlet.api.InstanceFactory
import io.undertow.servlet.api.InstanceHandle
import org.springframework.context.ConfigurableApplicationContext
import javax.servlet.Servlet

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
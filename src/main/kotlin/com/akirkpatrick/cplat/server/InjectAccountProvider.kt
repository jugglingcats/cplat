package com.akirkpatrick.cplat.server

import com.akirkpatrick.cplat.account.Account
import com.sun.jersey.core.spi.component.ComponentContext
import com.sun.jersey.core.spi.component.ComponentScope
import com.sun.jersey.spi.inject.Injectable
import com.sun.jersey.spi.inject.InjectableProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.lang.reflect.Type
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.Context
import javax.ws.rs.ext.Provider

/**
 * @author alfie
 */
@Component
@Provider
class InjectAccountProvider : InjectableProvider<InjectAccount, java.lang.reflect.Type> {
    val log=LoggerFactory.getLogger(javaClass<InjectAccountProvider>())

    @Context var request: HttpServletRequest? = null

    override fun getScope(): ComponentScope {
        return ComponentScope.PerRequest
    }

    override fun getInjectable(ic: ComponentContext, annotation: InjectAccount, c: Type): Injectable<*> {
        log.debug("Request for account by InjectAccount annotation")

        return Injectable<Account> {
            request!!.getParameter("test")
            Account(100, "Alfie")
        }
    }
}


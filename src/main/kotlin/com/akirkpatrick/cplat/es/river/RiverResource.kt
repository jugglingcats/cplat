package com.akirkpatrick.cplat.es.river

import com.akirkpatrick.cplat.es.ElasticSearchServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.ws.rs.DELETE
import javax.ws.rs.POST
import javax.ws.rs.Path

/**
 * @author alfie
 */
@Component
@Path("/river")
public class RiverResource @Autowired constructor(val es: ElasticSearchServer) {
    @POST
    fun create() {
        es.createRivers()
    }

    @DELETE
    fun delete() {

    }
}
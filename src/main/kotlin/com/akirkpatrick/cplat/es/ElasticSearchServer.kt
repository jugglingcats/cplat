package com.akirkpatrick.cplat.es

import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus
import org.elasticsearch.client.Client
import org.elasticsearch.client.Requests
import org.elasticsearch.common.settings.ImmutableSettings
import org.elasticsearch.node.NodeBuilder
import org.slf4j.LoggerFactory
import java.util.ArrayList
import java.util.Collections
import kotlin.collections.keySet
import kotlin.text.startsWith

public class ElasticSearchServer(val configuration: Map<String, String>) {

    private val builder: ImmutableSettings.Builder get() = ImmutableSettings.settingsBuilder().put(configuration)

    private val server = NodeBuilder.nodeBuilder().settings(builder).build()

    private val riverConfig = object {
        val type = "mongodb"
        val mongodb = object {
            val db = "mongo_test"
            val collection = "item"
            val options = object {
                val secondary_read_preference = true
            }
        }
        val index = object {
            val name="itemindex"
            val type="item"
        }

        fun toJson(): String {
            return ObjectMapper().writeValueAsString(this)
        }
    }

    public fun start() {
        if (log.isInfoEnabled()) {
            log.info("Starting the Elastic Search server node with these settings:")
            val map = server.settings().getAsMap()
            val keys = ArrayList(map.keySet())
            Collections.sort<String>(keys)
            for (key in keys) {
                log.info("    " + key + " : " + getValue(map, key))
            }
        }

        server.start()

        checkServerStatus()

//        createRivers()

        log.info("Elastic Search server is started")
    }

    public fun createRivers() {
        val s = riverConfig.toJson()
        log.info("Creating river: {}", s)

        val indexRequest = Requests.indexRequest("_river")
                .type("itemindex")
                .id("_meta")
                .source(s)

        server.client().index(indexRequest).actionGet()
    }

    public fun stop() {
        server.close()
    }

    public fun getClient(): Client {
        return server.client()
    }

    protected fun getHealthStatus(): ClusterHealthStatus {
        return getClient().admin().cluster().prepareHealth().execute().actionGet().getStatus()
    }


    protected fun checkServerStatus() {
        var status = getHealthStatus()

        // Check the current status of the ES cluster.
        if (ClusterHealthStatus.RED == status) {
            log.info("ES cluster status is " + status + ". Waiting for ES recovery.")

            // Waits at most 30 seconds to make sure the cluster health is at least yellow.
            getClient().admin().cluster().prepareHealth().setWaitForYellowStatus().setTimeout("30s").execute().actionGet()
        }

        // Check the cluster health for a final time.
        status = getHealthStatus()
        log.info("ES cluster status is " + status)

        // If we are still in red status, then we cannot proceed.
        if (ClusterHealthStatus.RED == status) {
            throw RuntimeException("ES cluster health status is RED. Server is not able to start.")
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(javaClass<ElasticSearchServer>())

        protected fun getValue(map: Map<String, String>, key: String): String? {
            if (key.startsWith("cloud.aws.secret")) {
                return "<HIDDEN>"
            }
            return map.get(key)
        }
    }
}
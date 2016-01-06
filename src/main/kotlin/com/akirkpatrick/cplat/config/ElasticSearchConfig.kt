package com.akirkpatrick.cplat.config

import com.akirkpatrick.cplat.es.ElasticSearchServer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.*

@Configuration
@Profile(ElasticSearchConfig.ES_ENABLED)
open public class ElasticSearchConfig() {

    open @Bean fun buildElasticSearchServer(): ElasticSearchServer {
        val map = HashMap<String, String>()
        map.put("path.work", "es-temp")
        return ElasticSearchServer(map)
    }

    companion object {
        const val ES_ENABLED="com.akirkpatrick.cplat.elasticsearch.enabled"
    }
}
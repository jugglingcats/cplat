package com.akirkpatrick.cplat.config

import com.mongodb.Mongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories("com.akirkpatrick.cplat")
open public class ApplicationConfig() {
    @Bean(name = arrayOf("mongoTemplate"))
    public open fun buildMongoTemplate(): MongoTemplate {
        var mongo = Mongo()
        return MongoTemplate(mongo, "mongo_test")
    }
}
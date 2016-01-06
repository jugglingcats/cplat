package com.akirkpatrick.cplat.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Component

/**
 * @author alfie
 */
@Component
class SequenceProvider @Autowired constructor(val mongoTemplate : MongoTemplate) {
    fun nextLong(name: String): Long {
        val query = Query(Criteria.where("_id").`is`(name));

        val update = Update();
        update.inc("seq", 1);

        val options = FindAndModifyOptions().returnNew(true);

        val seqId = mongoTemplate.findAndModify(query, update, options, javaClass<Sequence>());
        if ( seqId == null ) {
            // sequence not used before
            mongoTemplate.insert(Sequence(name, 1))
            return 1;
        }
        return seqId.seq
    }

    @Document(collection = "sequence")
    data class Sequence(@Id val id:String, val seq : Long)
}
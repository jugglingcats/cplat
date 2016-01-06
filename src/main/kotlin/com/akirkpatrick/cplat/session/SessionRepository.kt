package com.akirkpatrick.cplat.session

import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

/**
 * @author alfie
 */
public interface SessionRepository : MongoRepository<Session, UUID> {
}
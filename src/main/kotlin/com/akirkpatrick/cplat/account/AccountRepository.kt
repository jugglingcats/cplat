package com.akirkpatrick.cplat.account

import com.akirkpatrick.cplat.item.Item
import org.springframework.data.mongodb.repository.MongoRepository
import java.lang.String

/**
 * @author alfie
 */
public interface AccountRepository : MongoRepository<Account, String> {
    fun findById(id: Long) : Account?
    fun findByEmailAddress(email: kotlin.String) : Account?
}
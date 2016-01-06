package com.akirkpatrick.cplat.test

import com.akirkpatrick.cplat.account.Account
import com.akirkpatrick.cplat.account.AccountRepository
import com.akirkpatrick.cplat.config.SequenceProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * @author alfie
 */
@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration(classes = arrayOf(Tests.BaseTestConfig::class))
class Tests  {
    val log = LoggerFactory.getLogger(javaClass<Tests>())

    @Autowired var accounts : AccountRepository? = null
    @Autowired var seq : SequenceProvider? = null

    @Test fun testme() {
        val a= Account(100, "Alfie")
        a.email.add(Account.Email("alfie@akirkpatrick.com", true))

        accounts!!.save(a)

        val found=accounts!!.findByEmailAddress("alfie@akirkpatrick.com")
        log.info("Found: {}", found)
    }

    @Test fun testSequence() {
        val v=seq!!.nextLong("test")
        log.info("Sequence: {}", v)
    }

    @Configuration
    @ComponentScan(basePackages = arrayOf("com.akirkpatrick.cplat"))
    open class BaseTestConfig

}

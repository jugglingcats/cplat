package com.akirkpatrick.cplat.account

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.data.annotation.Id
import java.util.*

/**
 * @author alfie
 */
data class Account @JsonCreator constructor (@Id val id: Long, val name: String) {
    val email: LinkedList<Email> = LinkedList()

    data class Email @JsonCreator constructor(val address:kotlin.String, val active: Boolean)
}
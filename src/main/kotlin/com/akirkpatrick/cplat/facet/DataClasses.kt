package com.akirkpatrick.cplat.facet

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import java.util.*

/**
 * @author alfie
 */
data class FacetType @JsonCreator constructor(
        @Id @JsonProperty("code") val code: String,
        @JsonProperty("title") val title: String)

data class FacetValue @JsonCreator constructor(
        @Id @JsonProperty("id") var id: Long?,
        @JsonProperty("title") val title: String,
        @JsonProperty("type") val type: String) {

    @JsonProperty("linked")
    val linked = LinkedList<String>()
}
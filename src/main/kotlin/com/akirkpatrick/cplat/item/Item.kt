package com.akirkpatrick.cplat.item

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.annotation.Id
import java.util.*

public data class Item @JsonCreator constructor(@Id val id: String, val title: String) {
    private @JsonIgnore val properties = LinkedHashMap<String, Any>()

    val facet=LinkedHashMap<String, FacetRef>()

    public var votes : Int = 0

    public @JsonAnySetter fun set(name: String, value: Any) {
        properties.put(name, value)
    }

    public @JsonAnyGetter fun all(): Map<String, Any> {
        return properties
    }

    data class FacetRef(val id: Long, val value: Any)
}
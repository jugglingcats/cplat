package com.akirkpatrick.cplat.facet

import com.akirkpatrick.cplat.config.SequenceProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.ws.rs.*
import kotlin.collections.forEach
import kotlin.collections.setOf

/**
 * @author alfie
 */
@Component
@Path("/facet")
class FacetResource @Autowired constructor(val types: FacetTypeRepository, val values: FacetValueRepository, val seq: SequenceProvider) {
    val ROOT=java.lang.String("category")

    @POST
    @Path("/selection")
    fun selection(selection: LinkedHashMap<String, Long>?): Set<FacetType> {
        if ( selection == null || selection.isEmpty() ) {
            return setOf(types.findOne(ROOT))
        }

        val result=LinkedHashSet<FacetType>()
        selection.forEach {
            val value=values.findOne(it.value as java.lang.Number)
            result.add(types.findOne(java.lang.String(value.type)))
            value.linked.forEach {
                result.add(types.findOne(java.lang.String(it)))
            }
        }
        return result
    }

    @GET
    @Path("/type")
    fun types(): MutableList<FacetType> {
        return types.findAll()
    }

    @POST
    @Path("/type")
    fun saveType(type:FacetType) {
        types.save(type)
    }

    @DELETE
    @Path("/type/{code}")
    fun deleteType(@PathParam("code") code:java.lang.String) {
        types.delete(code)
    }

    @GET
    @Path("/value")
    fun values(): MutableList<FacetValue> {
        return values.findAll()
    }

    @GET
    @Path("/value/{code}")
    fun valuesByType(@PathParam("code") code:java.lang.String): MutableList<FacetValue> {
        return values.findByType(code)
    }

    @POST
    @Path("/value")
    fun saveValue(value:FacetValue) {
        if ( value.id == null ) {
            value.id=seq.nextLong("facet");
        }
        values.save(value)
    }

    @DELETE
    @Path("/value/{id}")
    fun deleteValue(@PathParam("id") id:java.lang.Long) {
        values.delete(id as java.lang.Number)
    }
}
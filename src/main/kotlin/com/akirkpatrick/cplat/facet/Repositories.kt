package com.akirkpatrick.cplat.facet

import org.springframework.data.mongodb.repository.MongoRepository

/**
 * @author alfie
 */
public interface FacetTypeRepository : MongoRepository<FacetType, java.lang.String> {
}

public interface FacetValueRepository : MongoRepository<FacetValue, java.lang.Number> {
    fun findByType(string: java.lang.String): MutableList<FacetValue>
}

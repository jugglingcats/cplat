package com.akirkpatrick.cplat.session

import com.fasterxml.jackson.annotation.JsonCreator
import org.springframework.data.annotation.Id
import java.util.*

public data class Session @JsonCreator constructor(@Id val id: UUID, val userId: Long, val expires: Date) {
}
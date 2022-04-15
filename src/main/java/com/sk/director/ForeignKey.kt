package com.sk.director

import com.sk.director.elements.EntityClass

data class ForeignKey(
    val entityClass: EntityClass<*>,
    val parentColumnName: String,
    val childColumnName: String,
)

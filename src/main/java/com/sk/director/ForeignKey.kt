package com.sk.director

data class ForeignKey(
    val entityClass: String,
    val parentColumnName: List<String>,
    val childColumnName: List<String>,
)

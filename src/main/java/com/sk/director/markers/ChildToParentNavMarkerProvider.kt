package com.sk.director.markers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.sk.director.elements.EntityClass

abstract class ChildToParentNavMarkerProvider : NavMarkerProvider() {

    override fun getMarkers(entity: EntityClass<*>): List<RelatedItemLineMarkerInfo<*>>? {
        val columnParameters = entity.getColumnParameters()
        val foreignKeyAnnotations = entity.getForeignKeyAnnotations()
        columnParameters?.forEach { columnParameter ->
            val foreignKeyAnnotation = foreignKeyAnnotations?.firstOrNull { x ->
                x.getChildColumnNames()?.contains(columnParameter.getName()) ?: false
            }
        }
        return emptyList()
    }
}
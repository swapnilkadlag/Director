package com.sk.director.markers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.sk.director.elements.EntityClass

abstract class ParentToChildNavMarkerProvider : NavMarkerProvider() {

    override fun getMarkers(entity: EntityClass<*>): List<RelatedItemLineMarkerInfo<*>>? {
        val foreignKeys = entity.getForeignKeyAnnotations()
        val columns = entity.getColumnParameters()
        columns?.forEach { column ->

        }
        return emptyList()
    }

}
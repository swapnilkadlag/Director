package com.sk.director.markers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.sk.director.Icons
import com.sk.director.elements.EntityClass
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty

abstract class ChildToParentNavMarkerProvider : NavMarkerProvider() {
    override fun getMarkers(entity: EntityClass<*>): List<RelatedItemLineMarkerInfo<*>>? {
        val columnParameters = entity.getColumnParameters()
        val foreignKeyAnnotations = entity.getForeignKeyAnnotations()
        val foreignKeyAnnotationData = foreignKeyAnnotations?.mapNotNull { it.getParentData() }?.flatten()
        return columnParameters?.mapNotNull { columnParameter ->
            val columnName = columnParameter.getName()
            val paramData = foreignKeyAnnotationData?.filter { x -> x.childColumnName == columnName }
            val targets = paramData?.mapNotNull {
                it.entityClass.getColumnParameters()?.firstOrNull { x -> x.getName() == it.parentColumnName }
            }?.map { it.element }
            targets?.ifNotEmpty {
                columnParameter.getNavigationElement()?.let {
                    NavigationGutterIconBuilder.create(Icons.Table)
                        .setTargets(this)
                        .setTooltipText("Navigate to parent table")
                        .createLineMarkerInfo(it)
                }
            }
        }
    }
}
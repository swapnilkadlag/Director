package com.sk.director.markers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.sk.director.Icons
import com.sk.director.ParentToChildMarkerCellRenderer
import com.sk.director.elements.EntityClass
import com.sk.director.elements.ForeignKeyAnnotation
import org.jetbrains.kotlin.utils.addToStdlib.ifNotEmpty

abstract class ParentToChildNavMarkerProvider : NavMarkerProvider() {
    override fun getMarkers(entity: EntityClass<*>): List<RelatedItemLineMarkerInfo<*>>? {
        val columnParameters = entity.getColumnParameters()
        val foreignKeyAnnotations = getReferencedAnnotations(entity.element)
        val fkData = foreignKeyAnnotations?.mapNotNull { it.getChildData() }?.flatten()
        return fkData?.groupBy { x -> x.parentColumnName }?.mapNotNull { entry ->
            val parentColumnParam = columnParameters?.firstOrNull { x -> x.getName() == entry.key }
            val childColumnParams = entry.value.mapNotNull { fk ->
                fk.entityClass.getColumnParameters()?.filter { it.getName() == fk.childColumnName }
            }.flatten()
            parentColumnParam?.getNavigationElement()?.let {
                NavigationGutterIconBuilder.create(Icons.Key)
                    .setTargets(childColumnParams.mapNotNull { p -> p.getNavigationElement() })
                    .setCellRenderer(ParentToChildMarkerCellRenderer())
                    .setTooltipText("Navigate to foreign keys")
                    .createLineMarkerInfo(it)
            }
        }
    }

    abstract fun getReferencedAnnotations(cls: PsiElement): List<ForeignKeyAnnotation<*>>?
}
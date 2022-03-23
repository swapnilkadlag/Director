package com.sk.director

import com.android.tools.idea.kotlin.findValueArgument
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*

class FKChildNavigationMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element is KtClass && element.isEntityClass()) {
            val project = element.project
            val psiShortNamesCache = PsiShortNamesCache.getInstance(project)
            val foreignKeyData = element.getForeignKeyData()
            val valueParameters = element.getValueParameters()

            val parentClasses = foreignKeyData.map { fk -> fk.entityClass }
            val parentClassElements = parentClasses.flatMap {
                psiShortNamesCache.getClassesByName(it, GlobalSearchScope.allScope(project)).asList()
            }.filterIsInstance<KtUltraLightClass>()

            valueParameters.forEach { p ->
                foreignKeyData.forEach {
                    if (it.childColumnName.contains(p.name)) {
                        val cls =
                            parentClassElements.first { x -> x.name == it.entityClass }
                        val graphLineMarker = NavigationGutterIconBuilder.create(PARENT_ICON)
                            .setTarget(cls)
                            .setTooltipText("Navigate to Simple language property")
                            .createLineMarkerInfo(p)
                        graphLineMarker.let { result.add(graphLineMarker) }
                    }
                }
            }
        }
    }
}

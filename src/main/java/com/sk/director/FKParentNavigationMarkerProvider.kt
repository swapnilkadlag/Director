package com.sk.director

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class FKParentNavigationMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element is KtClass && element.isEntityClass()) {
            val refs = ReferencesSearch.search(element)
            val childRefs = refs.map { it.element }.filter {  it is KtNameReferenceExpression }
            val parents = childRefs.mapNotNull {
                var parent = it.parent
                while (parent != null && !(parent is KtClass && parent.isEntityClass())){
                    parent = parent.parent
                }
                parent
            }
            parents.forEach {
                val graphLineMarker = NavigationGutterIconBuilder.create(CHILD_ICON)
                    .setTarget(it)
                    .setTooltipText("Navigate to Simple language property")
                    .createLineMarkerInfo(element)
                graphLineMarker.let { result.add(graphLineMarker) }
            }
        }
    }
}
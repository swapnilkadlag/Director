package com.sk.director

import com.android.annotations.concurrency.WorkerThread
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class FKParentNavigationMarkerProvider : RelatedItemLineMarkerProvider() {

    @WorkerThread
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element is KtClass && element.isEntityClass()) {
            val refs = ReferencesSearch.search(element)
            val childRefs = refs.map { it.element }.filterIsInstance<KtNameReferenceExpression>()
            val children = childRefs.mapNotNull {
                val parent = PsiTreeUtil.getParentOfType(it, KtClass::class.java)
                if (parent != null && parent.isEntityClass()) parent else null
            }
            val params = element.getValueParameters()
            children.forEach { child ->
                val foreignKeyData = child.getForeignKeyData().filter { x -> x.parentClassName == element.name }
                val childParameters = child.getValueParameters()
                foreignKeyData.forEach { foreignKey ->
                    params.forEach { param ->
                        val parentColumnIndex = foreignKey.parentColumns.indexOfFirst { x -> x == param.name }
                        if (parentColumnIndex != -1) {
                            val targetParam =
                                childParameters.firstOrNull { x -> x.name == foreignKey.childColumns[parentColumnIndex] }
                            if (targetParam != null) {
                                val graphLineMarker = param.valOrVarKeyword?.let { it1 ->
                                    NavigationGutterIconBuilder.create(CHILD_ICON)
                                        .setTarget(targetParam.identifyingElement)
                                        .setTooltipText("Navigate to parent key")
                                        .createLineMarkerInfo(it1)
                                }
                                graphLineMarker?.let { result.add(graphLineMarker) }
                            }
                        }
                    }
                }


            }
        }
    }
}
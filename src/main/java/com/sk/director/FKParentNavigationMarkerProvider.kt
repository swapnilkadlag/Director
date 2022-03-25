package com.sk.director

import com.android.annotations.concurrency.WorkerThread
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.ide.util.PsiElementListCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import javax.swing.DefaultListCellRenderer

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
            params.forEach { param ->
                val targetParams = mutableListOf<PsiElement>()
                children.forEach { child ->
                    val foreignKeyData = child.getForeignKeyData().filter { x -> x.parentClassName == element.name }
                    val childParameters = child.getValueParameters()
                    foreignKeyData.forEach { foreignKey ->
                        val parentColumnIndex = foreignKey.parentColumns.indexOfFirst { x -> x == param.name }
                        if (parentColumnIndex != -1) {
                            val targetParam =
                                childParameters.firstOrNull { x -> x.name == foreignKey.childColumns[parentColumnIndex] }
                            if (targetParam != null) {
                                targetParams.add(targetParam.identifyingElement!!)
                            }
                        }
                    }
                }
                if(targetParams.size > 0) {
                    val graphLineMarker = param.valOrVarKeyword?.let { it1 ->
                        NavigationGutterIconBuilder.create(CHILD_ICON)
                            .setTargets(targetParams)
                            .setPopupTitle("Foreign Keys")
                            .setTooltipText("Navigate to foreign key")
                            .setCellRenderer(Renderer())
                            .createLineMarkerInfo(it1)
                    }
                    graphLineMarker?.let { result.add(graphLineMarker) }
                }
            }
        }
    }
}

class Renderer : DefaultPsiElementCellRenderer() {
    override fun getElementText(element: PsiElement?): String? {
        val parent = PsiTreeUtil.getParentOfType(element, KtClass::class.java)
        return parent?.identifyingElement?.text
    }

    override fun getContainerText(element: PsiElement?, name: String?): String? {
        return element?.text
    }
}
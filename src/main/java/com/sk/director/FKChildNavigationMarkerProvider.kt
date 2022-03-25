package com.sk.director

import com.android.annotations.concurrency.WorkerThread
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache
import org.jetbrains.kotlin.asJava.classes.KtUltraLightClass
import org.jetbrains.kotlin.psi.*

class FKChildNavigationMarkerProvider : RelatedItemLineMarkerProvider() {

    @WorkerThread
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        if (element is KtClass && element.isEntityClass()) {
            val foreignKeyData = element.getForeignKeyData()
            val valueParameters = element.getValueParameters()
            valueParameters.forEach { param ->
                foreignKeyData.forEach { foreignKey ->
                    val childParamIndex = foreignKey.childColumns.indexOfFirst { x -> x == param.name }
                    val parentClass = foreignKey.targetClass as? KtClass
                    if (childParamIndex != -1 && parentClass != null) {
                        val classParams = parentClass.getValueParameters()
                        val parentParam = classParams.find { x -> x.name == foreignKey.parentColumns[childParamIndex] }
                        val graphLineMarker = param.valOrVarKeyword?.let { it1 ->
                            NavigationGutterIconBuilder.create(PARENT_ICON)
                                .setTarget(parentParam)
                                .setTooltipText("Navigate to parent key")
                                .createLineMarkerInfo(it1)
                        }
                        graphLineMarker?.let { m -> result.add(m) }
                    }
                }
            }
        }
    }
}

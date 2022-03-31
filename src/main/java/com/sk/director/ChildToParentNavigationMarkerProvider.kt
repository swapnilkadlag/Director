package com.sk.director

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType

class ChildToParentNavigationMarkerProvider : RelatedItemLineMarkerProvider() {

    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val entityClass = element as? KtClass ?: return
        val entityAnnotation = entityClass.getEntityAnnotation() ?: return
        val entityParams = entityClass.getConstructorParameters() ?: return
        val foreignKeysArg = entityAnnotation.getForeignKeyArgument() ?: return
        val markers = foreignKeysArg.getChildToParentForeignKeyData(childParams = entityParams)
        markers.forEach { marker ->
            NavigationGutterIconBuilder.create(Icons.Table)
                .setTargets(marker.targets)
                .setTooltipText("Navigate to parent key")
                .createLineMarkerInfo(marker.source)
                .also(result::add)
        }
    }

    private fun KtValueArgument.getChildToParentForeignKeyData(childParams: List<KtParameter>): List<SourceTargetMarkerData<PsiElement>> {
        val foreignKeyAnnotationCollection = getChildOfType<KtCollectionLiteralExpression>() ?: return emptyList()
        val foreignKeyAnnotations = foreignKeyAnnotationCollection.childrenOfType<KtCallExpression>()

        return foreignKeyAnnotations.mapNotNull { it.valueArgumentList }.mapNotNull { valueArgs ->
            val entityArg = valueArgs.findValueArgument(Parameters.Entity) ?: return@mapNotNull null
            val parentEntityExpression = entityArg.getChildOfType<KtClassLiteralExpression>() ?: return@mapNotNull null

            val parentColumnsArg = valueArgs.findValueArgument(Parameters.ParentColumns) ?: return@mapNotNull null
            val parentColumnNames = parentColumnsArg.getStrings() ?: return@mapNotNull null

            val childColumnsArg = valueArgs.findValueArgument(Parameters.ChildColumns) ?: return@mapNotNull null
            val childColumnNames = childColumnsArg.getStrings() ?: return@mapNotNull null

            if (parentColumnNames.size != childColumnNames.size) return@mapNotNull null

            val parentClass = parentEntityExpression.getReferencedClass() ?: return@mapNotNull null
            val parentParams = parentClass.getConstructorParameters() ?: return@mapNotNull null

            makeMarkers(
                sourceColumnNames = childColumnNames,
                targetColumnNames = parentColumnNames,
                sourceParams = childParams,
                targetParams = parentParams
            )
        }.flatten()
    }
}

package com.sk.director

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression


class ParentToChildNavigationMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val entityClass = element as? KtClass ?: return
        val entityParams = entityClass.getConstructorParameters() ?: return
        val references = ReferencesSearch.search(entityClass)
        val childReferences = references.map { it.element }.filterIsInstance<KtNameReferenceExpression>()
        val childForeignKeyAnnotation = childReferences.mapNotNull { nameReference ->
            PsiTreeUtil.getParentOfType(nameReference, KtCallExpression::class.java)
        }
        val childClasses = childForeignKeyAnnotation.mapNotNull { nameReference ->
            PsiTreeUtil.getParentOfType(nameReference, KtClass::class.java)?.let { cls ->
                val entityAnnotation = cls.getEntityAnnotation()
                if (entityAnnotation != null) cls else null
            }
        }

        val childParams = childClasses.flatMap { cls ->
            cls.getConstructorParameters() ?: emptyList()
        }
        val childColumns = childForeignKeyAnnotation.flatMap { annotation ->
            annotation.getChildColumns() ?: emptyList()
        }
        val parentColumns = childForeignKeyAnnotation.flatMap { annotation ->
            annotation.getParentColumns() ?: emptyList()
        }

        val foreignKeys = parentColumns.zip(childColumns) { p, c -> ForeignKey(p, c) }

        val markers = makeMarkers(
            sourceParams = entityParams,
            targetParams = childParams,
            foreignKeys = foreignKeys,
        )

        markers.forEach { marker ->
            NavigationGutterIconBuilder.create(Icons.Key)
                .setTargets(marker.targets)
                .setCellRenderer(ParentToChildMarkerCellRenderer())
                .setTooltipText("Navigate to child table")
                .createLineMarkerInfo(marker.source)
                .also(result::add)
        }
    }
}
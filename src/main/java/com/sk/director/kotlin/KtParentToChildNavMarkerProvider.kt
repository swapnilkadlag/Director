package com.sk.director.kotlin

import com.intellij.psi.PsiElement
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiTreeUtil
import com.sk.director.elements.EntityClass
import com.sk.director.elements.ForeignKeyAnnotation
import com.sk.director.markers.ParentToChildNavMarkerProvider
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

class KtParentToChildNavMarkerProvider : ParentToChildNavMarkerProvider() {
    override fun getEntity(element: PsiElement): EntityClass<*>? {
        return if (element is KtClass && element.isEntityClass()) {
            KtEntityClass(element)
        } else null
    }

    override fun getReferencedAnnotations(cls: PsiElement): List<ForeignKeyAnnotation<*>>? {
        val entityClass = cls as? KtClass ?: return null
        val references = ReferencesSearch.search(entityClass)
        val childReferences = references.map { it.element }.filterIsInstance<KtNameReferenceExpression>()
        val childForeignKeyAnnotation = childReferences.mapNotNull { nameReference ->
            PsiTreeUtil.getParentOfType(nameReference, KtCallExpression::class.java)
        }
        return childForeignKeyAnnotation.map(::KtForeignKeyAnnotation)
    }
}
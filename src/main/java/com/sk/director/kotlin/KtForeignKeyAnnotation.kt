package com.sk.director.kotlin

import com.intellij.psi.util.PsiTreeUtil
import com.sk.director.Parameters
import com.sk.director.elements.EntityClass
import com.sk.director.elements.ForeignKeyAnnotation
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

class KtForeignKeyAnnotation(ktCallExpression: KtCallExpression) :
    ForeignKeyAnnotation<KtCallExpression>(ktCallExpression) {
    override fun getName(): String? {
        TODO("Not yet implemented")
    }

    override fun getReferencedEntityClass(): EntityClass<*>? {
        val entityArgument = element.valueArgumentList?.findValueArgument(Parameters.Entity)
        val classReferenceExpression = entityArgument?.getChildOfType<KtClassLiteralExpression>()
        val classReference = classReferenceExpression?.getChildOfType<KtNameReferenceExpression>()
        val ktClass = classReference?.references?.firstIsInstanceOrNull<KtSimpleNameReference>()?.resolve() as? KtClass
        return ktClass?.let(::KtEntityClass)
    }

    override fun getParentEntityClass(): EntityClass<*>? {
        val ktClass = PsiTreeUtil.getParentOfType(element, KtClass::class.java)
        return ktClass?.let(::KtEntityClass)
    }

    override fun getChildColumnNames(): List<String>? {
        val childColumnsArgument = element.valueArgumentList?.findValueArgument(Parameters.ChildColumns)
        return childColumnsArgument?.getStrings()
    }

    override fun getParentColumnNames(): List<String>? {
        val parentColumnsArgument = element.valueArgumentList?.findValueArgument(Parameters.ParentColumns)
        return parentColumnsArgument?.getStrings()
    }
}
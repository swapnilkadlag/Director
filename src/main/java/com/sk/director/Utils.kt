package com.sk.director

import com.android.tools.idea.kotlin.findValueArgument
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

inline fun <reified T : PsiElement> PsiElement.findChildOfType(): T? {
    return children.filterIsInstance<T>().firstOrNull()
}

inline fun <reified T : PsiElement> PsiElement.getChildOfType(): T {
    return children.filterIsInstance<T>().first()
}

inline fun <reified T : PsiElement> PsiElement.getChildrenOfType(): List<T> {
    return children.filterIsInstance<T>()
}

fun KtClass.getValueParameters(): List<KtParameter> {
    val constructor = primaryConstructor ?: return emptyList()
    val constructorParameters = constructor.valueParameterList ?: return emptyList()
    return constructorParameters.parameters
}

fun KtClass.isEntityClass(): Boolean {
    return findAnnotation(FqName(ENTITY_ANNOTATION)) != null
}

fun KtClass.getEntityAnnotationEntry(): KtAnnotationEntry {
    return findAnnotation(FqName(ENTITY_ANNOTATION)) ?: throw IllegalStateException()
}

fun KtValueArgument.getCollectionLiteralExpression(): KtCollectionLiteralExpression? {
    return findChildOfType()
}

fun KtCollectionLiteralExpression.getCallExpressions(): List<KtCallExpression> {
    return getChildrenOfType()
}

fun KtCollectionLiteralExpression.getStringTemplateExpressions(): List<KtStringTemplateExpression> {
    return children.filterIsInstance<KtStringTemplateExpression>()
}

fun KtValueArgumentList.findValueArgument(argumentName: String): KtValueArgument? {
    return arguments.find { it.isOfName(argumentName) }
}

fun KtValueArgument.isOfName(name: String): Boolean {
    return getArgumentName()?.asName.toString() == name
}

fun KtStringTemplateExpression.getString(): String? {
    return entries.firstOrNull()?.text
}

fun KtClass.getForeignKeyData(): List<ForeignKey> {
    val entityAnnotation = this.getEntityAnnotationEntry()
    val foreignKeyParameter = entityAnnotation.findValueArgument(FOREIGN_KEYS) ?: return emptyList()
    val foreignKeyValue = foreignKeyParameter.getCollectionLiteralExpression() ?: return emptyList()
    val foreignKeyAnnotations = foreignKeyValue.getCallExpressions()
    val res = foreignKeyAnnotations.mapNotNull { fka ->
        val childColumnParameter = fka.valueArgumentList?.findValueArgument(CHILD_COLUMNS) ?: return emptyList()
        val childColumnValue = childColumnParameter.getCollectionLiteralExpression() ?: return emptyList()
        val childColumnNames = childColumnValue.getStringTemplateExpressions().mapNotNull { ste -> ste.getString() }
        val parentColumnParameter = fka.valueArgumentList?.findValueArgument(PARENT_COLUMNS) ?: return emptyList()
        val parentColumnValue = parentColumnParameter.getCollectionLiteralExpression() ?: return emptyList()
        val parentColumnNames = parentColumnValue.getStringTemplateExpressions().mapNotNull { ste -> ste.getString() }
        val entityParameter = fka.valueArgumentList?.findValueArgument(ENTITY) ?: return emptyList()
        val entityValue = entityParameter.findChildOfType<KtClassLiteralExpression>() ?: return emptyList()
        val entityName = entityValue.findChildOfType<KtNameReferenceExpression>()?.text ?: return emptyList()
        val targetClass = entityValue.children.first().references.firstIsInstanceOrNull<KtSimpleNameReference>()?.resolve()
        ForeignKey(entityName, parentColumnNames, childColumnNames, targetClass)
    }
    return res
}
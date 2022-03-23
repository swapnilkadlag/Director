package com.sk.director

import com.android.tools.idea.kotlin.findValueArgument
import com.intellij.openapi.util.IconLoader
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.*


val PARENT_ICON = IconLoader.getIcon("/icons/table.svg")
val CHILD_ICON = IconLoader.getIcon("/icons/key.svg")

fun KtClass.getForeignKeyData(): List<ForeignKey> {
    val entityAnnotation = getEntityAnnotationEntry()
    val foreignKeyParameter = entityAnnotation.findValueArgument(FOREIGN_KEYS) ?: return emptyList()
    val foreignKeyValue = foreignKeyParameter.getCollectionLiteralExpression() ?: return emptyList()
    val foreignKeyAnnotations = foreignKeyValue.children.filterIsInstance<KtCallExpression>()
    val res = foreignKeyAnnotations.map { fka ->
        val childColumnParameter = fka.valueArgumentList?.findValueArgument(CHILD_COLUMNS) ?: return emptyList()
        val childColumnValue = childColumnParameter.getCollectionLiteralExpression() ?: return emptyList()
        val childColumnNames = childColumnValue.getStringTemplateExpressions().mapNotNull { ste -> ste.getString() }
        val parentColumnParameter = fka.valueArgumentList?.findValueArgument(PARENT_COLUMNS) ?: return emptyList()
        val parentColumnValue = parentColumnParameter.getCollectionLiteralExpression() ?: return emptyList()
        val parentColumnNames = parentColumnValue.getStringTemplateExpressions().mapNotNull { ste -> ste.getString() }
        val entityParameter = fka.valueArgumentList?.findValueArgument(ENTITY) ?: return emptyList()
        val entityValue =
            entityParameter.children.filterIsInstance<KtClassLiteralExpression>().firstOrNull() ?: return emptyList()
        val entityName =
            entityValue.children.filterIsInstance<KtNameReferenceExpression>().firstOrNull()?.text ?: return emptyList()
        ForeignKey(entityName, parentColumnNames, childColumnNames)
    }
    return res
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
    return children.filterIsInstance<KtCollectionLiteralExpression>().firstOrNull()
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

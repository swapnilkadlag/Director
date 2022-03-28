package com.sk.director

import com.android.tools.idea.kotlin.findValueArgument
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.idea.references.KtSimpleNameReference
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType


fun KtClass.getEntityAnnotation(): KtAnnotationEntry? {
    return findAnnotation(Annotations.EntityFQName)
}

fun KtAnnotationEntry.getForeignKeyArgument(): KtValueArgument? {
    return findValueArgument(Parameters.ForeignKeys)
}

fun KtStringTemplateExpression.getString(): String? {
    return entries.firstOrNull()?.text
}

fun KtValueArgument.getStringList(): List<String>? {
    val collectionExpression = getChildOfType<KtCollectionLiteralExpression>()
    val stringTemplates = collectionExpression?.childrenOfType<KtStringTemplateExpression>()
    return stringTemplates?.mapNotNull { it.getString() }
}

fun KtClass.getConstructorParameters(): List<KtParameter>? {
    val constructorParameters = primaryConstructor?.valueParameterList
    return constructorParameters?.parameters
}

fun KtValueArgumentList.findValueArgument(argumentName: String): KtValueArgument? {
    return arguments.find { it.isOfName(argumentName) }
}

fun KtValueArgument.isOfName(name: String): Boolean {
    return getArgumentName()?.asName.toString() == name
}

fun KtClassLiteralExpression.getReferencedClass(): KtClass? {
    val classReference = getChildOfType<KtNameReferenceExpression>()
    return classReference?.references?.firstIsInstanceOrNull<KtSimpleNameReference>()?.resolve() as? KtClass
}

fun KtCallExpression.getChildColumns(): List<String>? {
    val columnsArg = valueArgumentList?.findValueArgument(Parameters.ChildColumns)
    return columnsArg?.getStringList()
}

fun KtCallExpression.getParentColumns(): List<String>? {
    val columnsArg = valueArgumentList?.findValueArgument(Parameters.ParentColumns)
    return columnsArg?.getStringList()
}

fun makeMarkers(
    sourceParams: List<KtParameter>,
    targetParams: List<KtParameter>,
    sourceColumnNames: List<String>,
    targetColumnNames: List<String>
): List<SourceTargetMarkerData<PsiElement>>? {
    if (sourceColumnNames.size != targetColumnNames.size) return null
    return sourceColumnNames.zip(targetColumnNames) { sourceColumnName, targetColumnName ->
        val source = sourceParams.firstOrNull { x -> x.name == sourceColumnName }?.valOrVarKeyword ?: return@zip null
        val targets = targetParams.filter { x -> x.name == targetColumnName }.mapNotNull { it.valOrVarKeyword }
        SourceTargetMarkerData(source, targets)
    }.filterNotNull()
}


fun makeMarkers(
    sourceParams: List<KtParameter>,
    targetParams: List<KtParameter>,
    foreignKeys: List<ForeignKey>,
): List<SourceTargetMarkerData<PsiElement>> {
    return sourceParams.mapNotNull { param ->
        val paramForeignKeys = foreignKeys.filter { x -> x.parentColumnName == param.name }
        val childParams = paramForeignKeys.flatMap { x -> targetParams.filter { it.name == x.childColumnName } }
        if (childParams.isNotEmpty() && param.valOrVarKeyword != null) {
            SourceTargetMarkerData(param.valOrVarKeyword!!, childParams)
        } else null
    }
}


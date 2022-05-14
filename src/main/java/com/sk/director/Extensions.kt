package com.sk.director

import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType

fun KtClass.isEntityClass(): Boolean {
    return this.findAnnotation(Annotations.EntityFQName) != null
}

fun KtStringTemplateExpression.getString(): String? {
    return entries.firstOrNull()?.text
}

fun KtValueArgument.getStrings(): List<String>? {
    val collectionExpression = getChildOfType<KtCollectionLiteralExpression>()
    val stringTemplates = collectionExpression?.childrenOfType<KtStringTemplateExpression>()
    return stringTemplates?.mapNotNull { it.getString() }
}

fun KtValueArgumentList.findValueArgument(argumentName: String): KtValueArgument? {
    return arguments.find { it.isOfName(argumentName) }
}

fun KtValueArgument.isOfName(name: String): Boolean {
    return getArgumentName()?.asName.toString() == name
}



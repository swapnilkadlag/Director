package com.sk.director.kotlin

import com.android.tools.idea.kotlin.findValueArgument
import com.sk.director.*
import com.sk.director.elements.ColumnParameter
import com.sk.director.elements.EntityClass
import com.sk.director.elements.ForeignKeyAnnotation
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.getChildOfType
import org.jetbrains.plugins.groovy.lang.psi.util.childrenOfType

class KtEntityClass(element: KtClass) : EntityClass<KtClass>(element) {

    override fun getColumnParameters(): List<ColumnParameter<*>> {
        val constructorParameters = element.primaryConstructorParameters
        val constructorColumns = constructorParameters.map(::KtColumnParameter)
        val properties = element.getProperties()
        val propertyColumns = properties.map(::KtColumnProperty)
        return constructorColumns + propertyColumns
    }

    override fun getName(): String? {
        return element.nameIdentifier?.text
    }

    override fun getForeignKeyAnnotations(): List<ForeignKeyAnnotation<*>>? {
        val entityAnnotation = element.findAnnotation(Annotations.EntityFQName)
        val foreignKeysArgument = entityAnnotation?.findValueArgument(Parameters.ForeignKeys)
        val foreignKeyAnnotationCollection = foreignKeysArgument?.getChildOfType<KtCollectionLiteralExpression>()
        val foreignKeyAnnotations = foreignKeyAnnotationCollection?.childrenOfType<KtCallExpression>()
        return foreignKeyAnnotations?.map(::KtForeignKeyAnnotation)
    }
}
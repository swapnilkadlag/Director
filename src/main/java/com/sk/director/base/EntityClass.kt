package com.sk.director.base

import com.intellij.psi.PsiElement

abstract class EntityClass<T : PsiElement>(element: T) : Element<T>(element) {

    abstract fun getForeignKeyAnnotations() : List<ForeignKeyAnnotation<*>>?

    abstract fun getColumnParameters(): List<ColumnParameter<*>>?
}
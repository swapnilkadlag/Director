package com.sk.director.elements

import com.intellij.psi.PsiElement

abstract class ForeignKeyAnnotation<T : PsiElement>(element: T) : Element<T>(element) {

    abstract fun getReferencedEntityClass(): EntityClass<*>?

    abstract fun getParentEntityClass(): EntityClass<*>?

    abstract fun getParentColumnNames(): List<String>?

    abstract fun getChildColumnNames(): List<String>?
}
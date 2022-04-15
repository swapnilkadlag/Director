package com.sk.director.elements

import com.intellij.psi.PsiElement
import com.sk.director.ForeignKey

abstract class ForeignKeyAnnotation<T : PsiElement>(element: T) : Element<T>(element) {

    abstract fun getReferencedEntityClass(): EntityClass<*>?

    abstract fun getParentEntityClass(): EntityClass<*>?

    abstract fun getParentColumnNames(): List<String>?

    abstract fun getChildColumnNames(): List<String>?

    fun getParentData(): List<ForeignKey>? {
        val parentClass = getReferencedEntityClass() ?: return null
        val childColumns = getChildColumnNames() ?: return null
        val parentColumns = getParentColumnNames() ?: return null
        return parentColumns.zip(childColumns) { p, c ->
            ForeignKey(parentClass, p, c)
        }
    }

    fun getChildData(): List<ForeignKey>? {
        val parentClass = getParentEntityClass() ?: return null
        val childColumns = getChildColumnNames() ?: return null
        val parentColumns = getParentColumnNames() ?: return null
        return parentColumns.zip(childColumns) { p, c ->
            ForeignKey(parentClass, p, c)
        }
    }
}
package com.sk.director.base

import com.intellij.psi.PsiElement
import com.sk.director.base.Element
import com.sk.director.base.ColumnElement

abstract class TableElement<T : PsiElement>(element: T) : Element<T>(element) {
    abstract fun getColumns(): List<ColumnElement<*>>
}
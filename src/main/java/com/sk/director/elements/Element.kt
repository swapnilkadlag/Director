package com.sk.director.elements

import com.intellij.psi.PsiElement

abstract class Element<T : PsiElement>(val element: T) {
    abstract fun getName(): String?
}
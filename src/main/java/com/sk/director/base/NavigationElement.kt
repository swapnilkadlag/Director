package com.sk.director.base

import com.intellij.psi.PsiElement

abstract class NavigationElement<T:PsiElement>(element: T) : Element<T>(element) {

    abstract fun getNavigationElement(): PsiElement?
}
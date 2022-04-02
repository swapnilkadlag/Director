package com.sk.director.elements

import com.intellij.psi.PsiElement

abstract class NavigationElement<T:PsiElement>(element: T) : Element<T>(element) {

    abstract fun getNavigationElement(): PsiElement?
}
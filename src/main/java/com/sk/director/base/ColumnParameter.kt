package com.sk.director.base

import com.intellij.psi.PsiElement

abstract class ColumnParameter<T : PsiElement>(element: T) : NavigationElement<T>(element) {

}
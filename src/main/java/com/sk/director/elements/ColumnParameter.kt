package com.sk.director.elements

import com.intellij.psi.PsiElement

abstract class ColumnParameter<T : PsiElement>(element: T) : NavigationElement<T>(element)
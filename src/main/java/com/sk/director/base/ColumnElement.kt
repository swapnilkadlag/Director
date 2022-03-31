package com.sk.director.base

import com.intellij.psi.PsiElement
import com.sk.director.base.Element

abstract class ColumnElement<T : PsiElement>(element: T) : NavigationElement<T>(element) {

}
package com.sk.director

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.sk.director.elements.EntityClass

data class ForeignKey(
    val parentClass: EntityClass<*>,
    val parentColumnName: String,
    val childColumnName: String,
)

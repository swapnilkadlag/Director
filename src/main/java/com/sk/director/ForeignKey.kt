package com.sk.director

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

data class ForeignKey(
    val parentColumnName: String,
    val childColumnName: String,
)

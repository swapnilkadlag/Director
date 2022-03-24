package com.sk.director

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference

data class ForeignKey(
    val parentClassName: String,
    val parentColumns: List<String>,
    val childColumns: List<String>,
    val targetClass : PsiElement?,
)

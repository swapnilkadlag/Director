package com.sk.director

import com.intellij.psi.PsiElement

data class SourceTargetMarkerData<T : PsiElement>(
    val source: T,
    val targets: List<T>,
)
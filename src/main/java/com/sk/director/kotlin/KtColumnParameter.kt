package com.sk.director.kotlin

import com.intellij.psi.PsiElement
import com.sk.director.elements.ColumnParameter
import org.jetbrains.kotlin.psi.KtParameter

class KtColumnParameter(element: KtParameter) : ColumnParameter<KtParameter>(element) {
    override fun getNavigationElement(): PsiElement? {
        return element.valOrVarKeyword
    }

    override fun getName(): String? {
        return element.nameIdentifier?.text
    }
}
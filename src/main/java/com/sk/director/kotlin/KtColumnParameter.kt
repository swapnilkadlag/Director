package com.sk.director.kotlin

import com.android.tools.idea.kotlin.findValueArgument
import com.intellij.psi.PsiElement
import com.sk.director.Annotations
import com.sk.director.Parameters
import com.sk.director.elements.ColumnParameter
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.psi.KtParameter

class KtColumnParameter(element: KtParameter) : ColumnParameter<KtParameter>(element) {
    override fun getNavigationElement(): PsiElement? {
        return element.identifyingElement
    }

    override fun getName(): String? {
        val columnInfoAnnotation = element.findAnnotation(Annotations.ColumnInfoFQName)
        return if (columnInfoAnnotation != null) {
            val nameArgument = columnInfoAnnotation.findValueArgument(Parameters.Name)
            nameArgument?.stringTemplateExpression?.getString() ?: element.nameIdentifier?.text
        } else element.nameIdentifier?.text
    }
}
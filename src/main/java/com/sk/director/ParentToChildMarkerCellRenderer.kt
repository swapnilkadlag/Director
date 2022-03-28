package com.sk.director

import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.psi.KtClass

class ParentToChildMarkerCellRenderer : DefaultPsiElementCellRenderer() {
    override fun getElementText(element: PsiElement?): String? {
        val parent = PsiTreeUtil.getParentOfType(element, KtClass::class.java)
        return parent?.identifyingElement?.text
    }

    override fun getContainerText(element: PsiElement?, name: String?): String? {
        return element?.text
    }
}
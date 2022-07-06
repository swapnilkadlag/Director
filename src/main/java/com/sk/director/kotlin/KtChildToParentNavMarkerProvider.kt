package com.sk.director.kotlin

import com.intellij.psi.PsiElement
import com.sk.director.elements.EntityClass
import com.sk.director.markers.ChildToParentNavMarkerProvider
import org.jetbrains.kotlin.psi.KtClass

class KtChildToParentNavMarkerProvider : ChildToParentNavMarkerProvider() {
    override fun getEntity(element: PsiElement): EntityClass<*>? {
        return if (element is KtClass && element.isEntityClass()) {
            KtEntityClass(element)
        } else null
    }
}
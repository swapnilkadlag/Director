package com.sk.director.markers

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.psi.PsiElement
import com.sk.director.elements.EntityClass

abstract class NavMarkerProvider : RelatedItemLineMarkerProvider() {

    abstract fun getEntity(element: PsiElement): EntityClass<*>?

    abstract fun getMarkers(entity: EntityClass<*>): List<RelatedItemLineMarkerInfo<*>>?

    final override fun collectNavigationMarkers(
        element: PsiElement,
        result: MutableCollection<in RelatedItemLineMarkerInfo<*>>
    ) {
        val entity = getEntity(element) ?: return
        val markers = getMarkers(entity) ?: return
        result.addAll(markers)
    }
}
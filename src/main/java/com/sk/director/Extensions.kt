package com.sk.director

import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.psi.KtClass


fun KtClass.isEntityClass(): Boolean {
    return this.findAnnotation(Annotations.EntityFQName) != null
}
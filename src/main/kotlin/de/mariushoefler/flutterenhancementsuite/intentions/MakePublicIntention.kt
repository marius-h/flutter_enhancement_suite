package de.mariushoefler.flutterenhancementsuite.intentions

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.lang.dart.psi.DartNamedConstructorDeclaration

/**
 * Makes selected class, function or variable public
 *
 * @author Marius Höfler
 * @since v1.6.0
 */
class MakePublicIntention : AbstractModifyVisibilityIntentionAction() {
    override fun getText(): String = "Make public"

    override fun getModifiedName(element: PsiElement): String = element.text.removePrefix(PRIVATE_MODIFIER.toString())

    override fun isAvailable(element: PsiElement): Boolean {
        return element.text.startsWith(
            PRIVATE_MODIFIER
        ) && element.parentOfType<DartNamedConstructorDeclaration>() == null
    }
}

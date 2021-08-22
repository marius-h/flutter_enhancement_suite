package de.mariushoefler.flutterenhancementsuite.intentions

import com.intellij.psi.PsiElement

/**
 * Makes selected class, function or variable private
 *
 * @author Marius Höfler
 * @since v1.6.0
 */
class MakePrivateIntention : AbstractModifyVisibilityIntentionAction() {
    override fun getText(): String = "Make private"

    override fun getModifiedName(element: PsiElement): String = PRIVATE_MODIFIER + element.text

    override fun isAvailable(element: PsiElement): Boolean {
        return !element.text.startsWith(PRIVATE_MODIFIER)
    }
}

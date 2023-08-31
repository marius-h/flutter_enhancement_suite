package de.mariushoefler.flutterenhancementsuite.intentions

import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.jetbrains.lang.dart.psi.DartMethodDeclaration
import com.jetbrains.lang.dart.psi.DartNamedConstructorDeclaration

/**
 * Makes selected class, function or variable private
 *
 * @author Marius Höfler
 * @since v1.6.0
 */
class MakePrivateIntention : AbstractModifyVisibilityIntentionAction() {
    override fun getText(): String = "Make private"

    override fun getModifiedName(element: PsiElement): String {
        return if (element.parentOfType<DartMethodDeclaration>()?.isConstructor == true) {
            PRIVATE_MODIFIER.toString()
        } else PRIVATE_MODIFIER + element.text
    }

    override fun isAvailable(element: PsiElement): Boolean {
        return !element.text.startsWith(
            PRIVATE_MODIFIER
        ) && element.parentOfType<DartNamedConstructorDeclaration>() == null
    }
}

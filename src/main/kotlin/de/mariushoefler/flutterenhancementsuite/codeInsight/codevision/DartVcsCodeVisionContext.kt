package de.mariushoefler.flutterenhancementsuite.codeInsight.codevision

import com.intellij.codeInsight.hints.VcsCodeVisionLanguageContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import de.mariushoefler.flutterenhancementsuite.utils.enablesCodeVision
import java.awt.event.MouseEvent

/**
 * This class is responsible for the code vision feature in the VCS.
 * It is used to show the git history of a method.
 *
 * @author Marius Höfler
 * @since v1.7.0
 */
@Suppress("UnstableApiUsage")
class DartVcsCodeVisionContext : VcsCodeVisionLanguageContext {
    override fun handleClick(mouseEvent: MouseEvent, editor: Editor, element: PsiElement) {}

    override fun isAccepted(element: PsiElement): Boolean {
        return element.enablesCodeVision()
    }
}

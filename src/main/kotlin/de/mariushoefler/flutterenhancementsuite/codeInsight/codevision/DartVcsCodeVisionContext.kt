package de.mariushoefler.flutterenhancementsuite.codeInsight.codevision

import com.intellij.codeInsight.hints.VcsCodeVisionLanguageContext
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import de.mariushoefler.flutterenhancementsuite.utils.enablesCodeVision
import java.awt.event.MouseEvent

@Suppress("UnstableApiUsage")
class DartVcsCodeVisionContext : VcsCodeVisionLanguageContext {
    override fun handleClick(mouseEvent: MouseEvent, editor: Editor, element: PsiElement) {}

    override fun isAccepted(element: PsiElement): Boolean {
        return element.enablesCodeVision()
    }
}

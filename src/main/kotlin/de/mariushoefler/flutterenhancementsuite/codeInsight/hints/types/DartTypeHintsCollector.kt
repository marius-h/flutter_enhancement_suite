package de.mariushoefler.flutterenhancementsuite.codeInsight.hints.types

import com.intellij.codeInsight.hints.FactoryInlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.childrenOfType
import com.jetbrains.lang.dart.analyzer.DartAnalysisServerService
import com.jetbrains.lang.dart.psi.DartComponentName
import com.jetbrains.lang.dart.psi.DartSimpleFormalParameter
import com.jetbrains.lang.dart.psi.DartType
import com.jetbrains.lang.dart.psi.DartVarAccessDeclaration

/**
 * This class is responsible for collecting type hints for Dart.
 *
 * @author Marius Höfler
 * @since v1.7.0
 */
@Suppress("UnstableApiUsage")
class DartTypeHintsCollector(
    editor: Editor,
    private val file: PsiFile,
    private val settings: DartTypeInlayHintsProvider.Settings
) :
    FactoryInlayHintsCollector(editor) {
    override fun collect(element: PsiElement, editor: Editor, sink: InlayHintsSink): Boolean {
        if (element is DartVarAccessDeclaration || element is DartSimpleFormalParameter) {
            if (element.childrenOfType<DartType>().isNotEmpty()) return true
            val identifier =
                element.childrenOfType<DartComponentName>().firstOrNull() ?: return true
            if (file.virtualFile != null) {
                findStaticType(identifier)?.let { type ->
                    submitInlayHint(identifier, type, sink)
                }
            } else {
                submitInlayHint(identifier, "foo", sink)
            }
        }
        return true
    }

    private fun findStaticType(identifier: DartComponentName): String? {
        return DartAnalysisServerService.getInstance(file.project)
            .analysis_getHover(file.virtualFile, identifier.textOffset)
            .firstOrNull()?.staticType
    }

    private fun submitInlayHint(
        identifier: DartComponentName,
        type: String,
        sink: InlayHintsSink
    ) {
        val identifierRange = identifier.textRange
        val typeRepresentation = factory.smallText(type)
        val (offset, representation) = if (settings.insertBeforeIdentifier) {
            identifierRange.startOffset to factory.seq(
                factory.roundWithBackground(typeRepresentation), factory.textSpacePlaceholder(1, true)
            )
        } else {
            identifierRange.endOffset to factory.roundWithBackground(
                factory.seq(factory.smallText(": "), typeRepresentation)
            )
        }
        sink.addInlineElement(
            offset, true, representation, false
        )
    }
}

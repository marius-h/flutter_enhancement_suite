package de.mariushoefler.flutterenhancementsuite.codeInsight.codevision

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.hints.codeVision.ReferencesCodeVisionProvider
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.find.findUsages.FindUsagesOptions
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.awt.RelativePoint
import com.jetbrains.lang.dart.ide.findUsages.DartServerFindUsagesHandler
import com.jetbrains.lang.dart.psi.DartComponent
import com.jetbrains.lang.dart.psi.DartFile
import com.jetbrains.lang.dart.psi.DartVarDeclarationList
import com.jetbrains.lang.dart.test.DartTestSourcesFilter
import de.mariushoefler.flutterenhancementsuite.utils.enablesCodeVision
import java.awt.event.MouseEvent
import java.util.concurrent.atomic.AtomicInteger

/**
 * This class is responsible for providing usages of elements in the code vision feature.
 *
 * @author Marius Höfler
 * @since v1.7.0
 */
class DartReferencesCodeVisionProvider : ReferencesCodeVisionProvider() {
    companion object {
        const val ID = "dart.references"
        private const val MAX_USAGES = 100
    }

    override val id: String
        get() = ID

    override fun acceptsFile(file: PsiFile) = file is DartFile

    override fun acceptsElement(element: PsiElement): Boolean {
        return element.enablesCodeVision()
    }

    override fun getHint(element: PsiElement, file: PsiFile): String? {
        val el = if (element is DartVarDeclarationList) element.varAccessDeclaration else element as? DartComponent
            ?: return null
        val referencedElement = el.componentName ?: return null

        val scope = GlobalSearchScope.projectScope(element.project)
        val usages = AtomicInteger()
        val testUsages = AtomicInteger()
        val finder = DartServerFindUsagesHandler(element)
        val options = FindUsagesOptions(scope)
        options.isUsages = true
        options.isSearchForTextOccurrences = false
        finder.processElementUsages(referencedElement, {
            it.element?.let { element ->
                if (DartTestSourcesFilter.isTestSources(element.containingFile.virtualFile, element.project)) {
                    testUsages.incrementAndGet()
                }
            }
            return@processElementUsages usages.incrementAndGet() <= MAX_USAGES
        }, options)

        val sourceUsagesLabel = when (val count = usages.get()) {
            0 -> if (!el.isAbstract) "No usages" else return null
            1 -> "1 usage"
            else -> "$count usages"
        }

        val testUsagesLabel = when (val count = testUsages.get()) {
            0 -> return sourceUsagesLabel
            else -> "$count in tests"
        }

        return "$sourceUsagesLabel ($testUsagesLabel)"
    }

    override fun handleClick(editor: Editor, element: PsiElement, event: MouseEvent?) {
        val actualElement = if (element is DartVarDeclarationList) element.varAccessDeclaration else element
        GotoDeclarationAction.startFindUsages(
            editor, element.project, actualElement, if (event == null) null else RelativePoint(event)
        )
    }

    override val relativeOrderings: List<CodeVisionRelativeOrdering>
        get() = emptyList()

}

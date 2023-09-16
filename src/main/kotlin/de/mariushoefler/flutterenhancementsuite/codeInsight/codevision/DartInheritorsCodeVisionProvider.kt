package de.mariushoefler.flutterenhancementsuite.codeInsight.codevision

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.daemon.DaemonBundle
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator
import com.intellij.codeInsight.hints.codeVision.InheritorsCodeVisionProvider
import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.lang.dart.DartBundle
import com.jetbrains.lang.dart.analyzer.DartAnalysisServerService
import com.jetbrains.lang.dart.ide.actions.DartInheritorsSearcher
import com.jetbrains.lang.dart.psi.DartClassDefinition
import com.jetbrains.lang.dart.psi.DartClassMembers
import com.jetbrains.lang.dart.psi.DartComponent
import com.jetbrains.lang.dart.psi.DartFile
import com.jetbrains.lang.dart.test.DartTestSourcesFilter
import com.jetbrains.lang.dart.util.DartResolveUtil
import java.awt.event.MouseEvent

/**
 * This class is responsible for providing implementation usages of elements in the code vision feature.
 *
 * @author Marius Höfler
 * @since v1.7.0
 */
class DartInheritorsCodeVisionProvider : InheritorsCodeVisionProvider() {
    companion object {
        const val ID = "dart.inheritors"
        val IMPLEMENTATIONS = Key<Set<DartComponent>>("IMPLEMENTATIONS_KEY")
    }

    override val id: String
        get() = ID

    override fun acceptsFile(file: PsiFile) = file is DartFile

    override fun acceptsElement(element: PsiElement): Boolean {
        if (!element.manager.isInProject(element)) return false

        return (element is DartComponent && element.parent is DartClassMembers && element.isAbstract) || (element is DartClassDefinition && element.isAbstract)
    }

    override fun getHint(element: PsiElement, file: PsiFile): String? {
        if (element !is DartComponent) return null

        val anchor = element.componentName ?: return null
        val project = element.project
        val das = DartAnalysisServerService.getInstance(project)
        val items = das.search_getTypeHierarchy(file.virtualFile, anchor.textRange.startOffset, false)
        if (items.isEmpty()) {
            return null
        }
        val implementations = when (element) {
            is DartClassDefinition -> DartInheritorsSearcher.getSubClasses(
                project, GlobalSearchScope.allScope(project), items
            )

            else -> DartInheritorsSearcher.getSubMembers(
                project, GlobalSearchScope.allScope(project), items
            )
        }
        element.putUserData(IMPLEMENTATIONS, implementations)

        val sourceImplementationsLabel = when (val count = implementations.size) {
            0 -> "No implementations"
            1 -> "1 implementation"
            else -> "$count implementations"
        }

        val testImplementationsLabel = when (val testImplementations =
            implementations.count { DartTestSourcesFilter.isTestSources(it.containingFile.virtualFile, it.project) }) {
            0 -> return sourceImplementationsLabel
            else -> "$testImplementations in tests"
        }

        return "$sourceImplementationsLabel ($testImplementationsLabel)"
    }

    override fun handleClick(editor: Editor, element: PsiElement, event: MouseEvent?) {
        if (event == null || element !is DartComponent) return

        val anchor = element.componentName ?: return
        val components = element.getUserData(IMPLEMENTATIONS) ?: return

        if (element is DartClassDefinition) {
            val popupTitle = DaemonBundle.message("navigation.title.subclass", anchor.name, components.size, "")
            val findUsagesTitle = DartBundle.message("tab.title.subclasses.of.0", anchor.name)
            openImplementationDialog(event, components, popupTitle, findUsagesTitle)
        } else {
            val popupTitle = DaemonBundle.message("navigation.title.overrider.method", anchor.name, components.size)
            val findUsagesTitle = DartBundle.message("tab.title.overriding.methods.of.0", anchor.name)
            openImplementationDialog(event, components, popupTitle, findUsagesTitle)
        }
    }

    private fun openImplementationDialog(
        event: MouseEvent,
        components: Set<DartComponent>,
        popupTitle: String,
        findUsagesTitle: String
    ) {
        PsiElementListNavigator.openTargets(
            event, DartResolveUtil.getComponentNameArray(components), popupTitle, findUsagesTitle,
            DefaultPsiElementCellRenderer()
        )
    }

    override val relativeOrderings: List<CodeVisionRelativeOrdering>
        get() = listOf(CodeVisionRelativeOrdering.CodeVisionRelativeOrderingAfter(DartReferencesCodeVisionProvider.ID))
}

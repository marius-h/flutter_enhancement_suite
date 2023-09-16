package de.mariushoefler.flutterenhancementsuite.editor

import com.intellij.lang.Language
import com.intellij.psi.ElementDescriptionUtil
import com.intellij.psi.PsiElement
import com.intellij.refactoring.util.RefactoringDescriptionLocation
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import com.jetbrains.lang.dart.DartLanguage
import com.jetbrains.lang.dart.psi.DartClassDefinition
import com.jetbrains.lang.dart.psi.DartFactoryConstructorDeclaration
import com.jetbrains.lang.dart.psi.DartMethodDeclaration
import com.jetbrains.lang.dart.psi.DartPsiCompositeElement
import com.jetbrains.lang.dart.psi.DartVarAccessDeclaration
import icons.FlutterIcons
import javax.swing.Icon

/**
 * Breadcrumbs Navigation Provider for Dart files
 *
 * @author Marius Höfler
 * @since v1.6.1
 */
class DartBreadcrumbsInfoProvider : BreadcrumbsProvider {
    override fun getLanguages(): Array<Language> = arrayOf(DartLanguage.INSTANCE)

    override fun acceptElement(e: PsiElement) = when (e) {
        is DartClassDefinition, is DartFactoryConstructorDeclaration, is DartMethodDeclaration, is DartVarAccessDeclaration -> {
            e is DartPsiCompositeElement && e.name != null
        }

        else -> false
    }

    override fun getElementInfo(e: PsiElement) = when (e) {
        is DartClassDefinition -> e.name!!
        is DartFactoryConstructorDeclaration -> "factory ${e.name}()"
        is DartMethodDeclaration -> "${e.name}()"
        is DartVarAccessDeclaration -> e.name!!
        else -> ""
    }

    override fun getElementIcon(element: PsiElement): Icon? {
        if (element is DartClassDefinition) {
            if (element.isAbstract) return FlutterIcons.CustomClassAbstract
            return FlutterIcons.CustomClass
        }
        return super.getElementIcon(element)
    }

    override fun getElementTooltip(element: PsiElement) =
        ElementDescriptionUtil.getElementDescription(element, RefactoringDescriptionLocation.WITH_PARENT)
}

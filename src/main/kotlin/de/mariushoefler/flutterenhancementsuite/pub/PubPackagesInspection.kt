package de.mariushoefler.flutterenhancementsuite.pub

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.utils.FileParser
import kotlinx.coroutines.runBlocking

class PubPackagesInspection : LocalInspectionTool() {

    override fun getDisplayName() = "Pub Packages latest versions"

    override fun getGroupDisplayName(): String = "group.names.modularization.issues"

    override fun getShortName() = "PubVersions"

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return YamlElementVisitor(holder, isOnTheFly)
    }

    override fun isEnabledByDefault() = true
}

class YamlElementVisitor(
    private val holder: ProblemsHolder,
    private val isOnTheFly: Boolean,
) : PsiElementVisitor() {

    override fun visitFile(file: PsiFile) {
        if (!isOnTheFly) return

        runBlocking {
            val fileParser = FileParser(file)
            val problemDescriptions = fileParser.checkFile()

            problemDescriptions.forEach {
                holder.showProblem(file, it.counter, it.latestVersion)
            }
        }
    }
}

private fun ProblemsHolder.showProblem(
    file: PsiFile,
    counter: Int,
    latestVersion: String
) {
    file.findElementAt(counter)?.let { psiElement ->
        println(psiElement)
        registerProblem(
            psiElement,
            "There's a new version available: $latestVersion",
            DependencyQuickFix(psiElement, latestVersion, true),
            DependencyQuickFix(psiElement, latestVersion, false)
        )
    }
}

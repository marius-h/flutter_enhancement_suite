package de.mariushoefler.flutterenhancementsuite.inspections

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiFile
import de.mariushoefler.flutterenhancementsuite.models.VersionDescription
import de.mariushoefler.flutterenhancementsuite.utils.FileParser
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile

/**
 * Package Update Availability Annotator
 *
 * @author Marius Höfler
 * @since v1.5.0
 */
class PackageUpdateExternalAnnotator : ExternalAnnotator<PsiFile, List<VersionDescription>>() {

    override fun collectInformation(file: PsiFile) = file

    override fun doAnnotate(psiFile: PsiFile?): List<VersionDescription> {
        return if (psiFile == null || !psiFile.isPubspecFile()) listOf()
        else FileParser(psiFile).checkFile()
    }

    override fun apply(file: PsiFile, annotationResult: List<VersionDescription>?, holder: AnnotationHolder) {
        val manager = InspectionManager.getInstance(file.project)

        annotationResult?.forEach {
            file.findElementAt(it.counter)?.let { psiElement ->
                val descriptor = manager.createProblemDescriptor(
                    psiElement, psiElement, "There's a new version available: ${it.latestVersion}",
                    ProblemHighlightType.WARNING,
                    true
                )
                holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Update package")
                    .range(psiElement)
                    .tooltip("There's a new version available: ${it.latestVersion}")
                    .problemGroup { "Flutter" }
                    .newLocalQuickFix(DependencyQuickFix(psiElement, it.latestVersion, true), descriptor)
                    .registerFix()
                    .newLocalQuickFix(DependencyQuickFix(psiElement, it.latestVersion, false), descriptor)
                    .registerFix()
                    .create()
            }
        }
    }
}

package de.mariushoefler.flutterenhancementsuite.editor

import com.intellij.openapi.paths.GlobalPathReferenceProvider
import com.intellij.openapi.paths.PathReferenceManager
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutterenhancementsuite.utils.isPubPackageName
import de.mariushoefler.flutterenhancementsuite.utils.isPubspecFile
import org.jetbrains.yaml.psi.YAMLKeyValue

const val PUB_PACKAGE_BASE_URL = "https://pub.dev/packages/"

/**
 * Package page reference contributor
 *
 * Creates a hyperlink for packages in pubspec.yaml which lead to the package's page on pub.dev.
 *
 * @author Marius Höfler
 * @since v1.6.0
 */
class PackageReferenceContributor : PsiReferenceContributor() {
    val globalPathProvider by lazy { PathReferenceManager.getInstance().globalWebPathReferenceProvider as GlobalPathReferenceProvider }

    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(YAMLKeyValue::class.java),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<PsiReference> {
                    val yamlKeyValue = element as YAMLKeyValue
                    return if (yamlKeyValue.text.isPubPackageName() && element.containingFile.isPubspecFile()) {
                        val references = mutableListOf<PsiReference>()
                        val packageName = yamlKeyValue.keyText

                        globalPathProvider.createUrlReference(
                            yamlKeyValue, PUB_PACKAGE_BASE_URL + packageName, TextRange.allOf(packageName),
                            references
                        )
                        references.toTypedArray()
                    } else PsiReference.EMPTY_ARRAY
                }
            })
    }
}

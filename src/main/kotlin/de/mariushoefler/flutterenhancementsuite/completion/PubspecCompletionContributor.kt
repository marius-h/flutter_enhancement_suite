package de.mariushoefler.flutterenhancementsuite.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.Editor
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiFile
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutterenhancementsuite.models.PubPackageSearch
import de.mariushoefler.flutterenhancementsuite.utils.FlutterProjectUtils
import de.mariushoefler.flutterenhancementsuite.utils.PubApi
import icons.DartIcons

class PubspecCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            PubspecCompletionProvider()
        )
    }

    override fun handleEmptyLookup(parameters: CompletionParameters, editor: Editor?): String {
        return "No packages found"
    }
}

class PubspecCompletionProvider : CompletionProvider<CompletionParameters>() {

    private var lastResults = arrayListOf<LookupElement>()
    private var lastSearchterm = ""
    var file: PsiFile? = null

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (file == null) {
            file = parameters.originalFile
        }

        file?.let { if (it.virtualFile.nameWithoutExtension != "pubspec") return }

        val userInput = result.prefixMatcher.prefix

        result.addLookupAdvertisement("Packages from pub.dev")
        result.restartCompletionOnAnyPrefixChange()

        if (lastSearchterm == userInput) {
            result.addAllElements(lastResults)
        } else if (userInput.length > 2) {
            lastSearchterm = userInput
            lastResults.clear()
            updateResults(userInput, result)
        }
    }

    private fun updateResults(userInput: String, result: CompletionResultSet) {
        for (page in 1..2) {
            val results = PubApi.searchPackage(userInput, page)?.packages ?: return
            results.forEach {
                if (it.name.contains(userInput, true)) {
                    createItem(it, result)
                }
            }
        }
    }

    private fun createItem(packageResult: PubPackageSearch.PubPackageResult, result: CompletionResultSet) {
        val packageName = packageResult.name.replaceFirst("dart:", "")
        PubApi.getPackage(packageName) { response ->
            response.body()?.let { pubPackage ->
                LookupElementBuilder
                    .create(pubPackage.generateDependencyString())
                    .withPresentableText("package: ${pubPackage.name}")
                    .withLookupString(pubPackage.name)
                    .withTypeText(pubPackage.latest.pubspec.getAuthorName(), true)
                    .withIcon(DartIcons.Dart_16)
                    .withInsertHandler { context, _ ->
                        context.editor.project?.let { project ->
                            file?.let {
                                FlutterProjectUtils.runPackagesGet(it, project)
                            }
                        }
                    }.also {
                        lastResults.add(it)
                        result.addElement(it)
                    }
            }
        }
    }
}

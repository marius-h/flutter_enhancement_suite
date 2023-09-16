package de.mariushoefler.flutterenhancementsuite.completion

import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiFile
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutterenhancementsuite.models.PubPackage
import de.mariushoefler.flutterenhancementsuite.models.PubScore
import de.mariushoefler.flutterenhancementsuite.utils.FlutterProjectUtils
import de.mariushoefler.flutterenhancementsuite.utils.PubApi
import de.mariushoefler.flutterenhancementsuite.utils.formatNumberWithK
import icons.DartIcons

private const val flutterFavoriteTag = "is:flutter-favorite"
private const val pluginTag = "is:plugin"

class PubspecCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC, PlatformPatterns.psiElement(), PubspecCompletionProvider()
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
        parameters: CompletionParameters, context: ProcessingContext, result: CompletionResultSet
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
                    val packageName = it.name.replaceFirst("dart:", "")
                    PubApi.getPackage(packageName) { response ->
                        response.body()?.let { pubPackage ->
                            PubApi.getPackageScore(packageName) { response ->
                                response.body()?.let { score ->
                                    createItem(pubPackage, score, result)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createItem(pubPackage: PubPackage, score: PubScore, result: CompletionResultSet) {
        val likeIcon =
            if (score.tags.contains(flutterFavoriteTag)) AllIcons.Ide.LikeSelected else AllIcons.Ide.LikeDimmed
        val icon = if (score.tags.contains(pluginTag)) AllIcons.Nodes.Plugin else DartIcons.Dart_16
        LookupElementBuilder
            .create(pubPackage.generateDependencyString())
            .withPresentableText(pubPackage.name)
            .withLookupString(pubPackage.name)
            .withTailText(pubPackage.latest.pubspec.getAuthorName(), true)
            .withBoldness(true)
            .withTypeText(formatNumberWithK(score.likeCount), likeIcon, false)
            .withTypeIconRightAligned(true)
            .withIcon(icon)
            .withCaseSensitivity(false)
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

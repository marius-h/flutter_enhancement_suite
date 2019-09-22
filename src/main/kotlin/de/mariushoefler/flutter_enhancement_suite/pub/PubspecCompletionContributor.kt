package de.mariushoefler.flutter_enhancement_suite.pub

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutter_enhancement_suite.models.PubPackageResult
import de.mariushoefler.flutter_enhancement_suite.utils.FlutterProjectUtils
import de.mariushoefler.flutter_enhancement_suite.utils.PubApi
import icons.DartIcons

class PubspecCompletionContributor : CompletionContributor() {

	init {
		extend(
				CompletionType.BASIC,
				PlatformPatterns.psiElement(),
				PubspecCompletionProvider()
		)
	}

	override fun handleEmptyLookup(parameters: CompletionParameters, editor: Editor?): String? {
		return "No packages found"
	}
}

class PubspecCompletionProvider : CompletionProvider<CompletionParameters>() {

	var lastResults = arrayListOf<LookupElement>()
	var lastSearchterm = ""
	var file: VirtualFile? = null

	override fun addCompletions(
			parameters: CompletionParameters,
			context: ProcessingContext,
			result: CompletionResultSet
	) {
		if (file == null) {
			file = parameters.originalFile.virtualFile
		}
		println(file?.nameWithoutExtension)

		file?.let { if (it.nameWithoutExtension != "pubspec") return }

		val userInput = result.prefixMatcher.prefix

		result.addLookupAdvertisement("Packages from pub.dev")
		result.restartCompletionOnAnyPrefixChange()

		if (lastSearchterm == userInput) {
			result.addAllElements(lastResults)
		} else if (userInput.length > 2) {
			lastSearchterm = userInput
			lastResults.clear()
			for (page in 1..2) {
				val results = PubApi.searchPackage(userInput, page)?.packages ?: return
				results.forEach {
					if (it.name.contains(userInput, true)) {
						createItem(it, result)
					}
				}
			}
		}
	}

	private fun createItem(packageResult: PubPackageResult, result: CompletionResultSet) {
		val packageName = packageResult.name.replaceFirst("dart:", "")
		val pubPackage = PubApi.getPackage(packageName)
		if (pubPackage != null) {
			println("Package '$packageName' added to list")
			val item = LookupElementBuilder
					.create(pubPackage.generateDependencyString())
					.withPresentableText("package: ${pubPackage.name}")
					.withLookupString(pubPackage.name)
					.withTypeText(pubPackage.getAuthorName(), true)
					.withIcon(DartIcons.Dart_16)
					.withInsertHandler { _, _ -> FlutterProjectUtils.runPackagesGet(file) }
			lastResults.add(item)
			result.addElement(item)
		}
	}
}
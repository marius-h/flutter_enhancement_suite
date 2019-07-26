package de.mariushoefler.flutter_enhancement_suite.pubassist

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import de.mariushoefler.flutter_enhancement_suite.models.PubPackageResult
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
}

class PubspecCompletionProvider : CompletionProvider<CompletionParameters>() {

	var lastResults = arrayListOf<LookupElement>()
	var lastSearchterm = ""

	override fun addCompletions(
			parameters: CompletionParameters,
			context: ProcessingContext,
			result: CompletionResultSet
	) {
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
					.withTypeText(pubPackage.authorName(), true)
					.withIcon(DartIcons.Dart_16)
			lastResults.add(item)
			result.addElement(item)
		}
	}
}

//data class TestModel(val test: String) {
//	class Deserializer : ResponseDeserializable<TestModel> {
//		override fun deserialize(content: String): TestModel? = Gson().fromJson(content, TestModel::class.java)
//	}
//}
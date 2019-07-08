package pubassist

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import pubassist.data.PubApi
import pubassist.models.PubPackage
import java.awt.Image
import java.net.URL
import javax.swing.Icon
import javax.swing.ImageIcon

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
	var icon: ImageIcon = ImageIcon(URL("https://raw.githubusercontent.com/dart-lang/site-shared/00e7b236fe753fd7b93e73b58ed86bc1008afd0e/src/_assets/images/dart/logo/1080px.png"))

	init {
		val image: Image = icon.image
		val newImage: Image = image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
		icon = ImageIcon(newImage)
	}

	override fun addCompletions(
			parameters: CompletionParameters,
			context: ProcessingContext,
			result: CompletionResultSet
	) {
		val userInput = result.prefixMatcher.prefix

		result.addLookupAdvertisement("Packages from pub.dev")

		if (userInput.length > 2) {
			val results = PubApi.searchPackage(userInput)?.packages

			results?.forEach { it ->
				val packageName = it.name.replaceFirst("dart:", "")
				val pubPackage = PubApi.getPackage(packageName)
				//println("${results.size} results found.")
				print("$packageName,")
				if (pubPackage != null) {
					result.addElement(LookupElementBuilder
							.create(pubPackage.generateDependencyString())
							.withPresentableText("package: ${pubPackage.name}")
							.withLookupString(pubPackage.name)
							.withTypeText(pubPackage.authorName(), true)
							.withIcon(icon)
					)
				}
			}
			println()
			println()
			result.stopHere()
		}
	}
}

data class TestModel(val test: String) {
	class Deserializer : ResponseDeserializable<TestModel> {
		override fun deserialize(content: String): TestModel? = Gson().fromJson(content, TestModel::class.java)
	}
}
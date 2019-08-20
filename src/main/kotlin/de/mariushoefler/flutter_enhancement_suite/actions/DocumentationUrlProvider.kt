package de.mariushoefler.flutter_enhancement_suite.actions

import com.github.kittinunf.fuel.httpGet
import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.progress.ProgressManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.FakePsiElement
import com.intellij.util.io.HttpRequests
import de.mariushoefler.flutter_enhancement_suite.editor.UrlAnnotator
import de.mariushoefler.flutter_enhancement_suite.utils.PubApi
import de.mariushoefler.flutter_enhancement_suite.utils.isPubPackageName
import de.mariushoefler.flutter_enhancement_suite.utils.isPubspecFile
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import org.jetbrains.kotlin.serialization.js.ast.JsAstProtoBuf
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Provides a preview of the package's documentation
 *
 * @since v1.2
 */
class UrlDocumentationProvider : AbstractDocumentationProvider() {

	override fun getDocumentationElementForLookupItem(psiManager: PsiManager, obj: Any?, element: PsiElement): PsiElement? {
		if (obj is String && element.containingFile.isPubspecFile()) {
			return SuggestionElement(psiManager, obj)
		}
		return null
	}

	override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
		val lookupString = when {
			element is SuggestionElement -> element.name
			element.text.isPubPackageName() -> element.firstChild.text
			else -> return null
		}

		val pubPackage = PubApi.lastPackages[lookupString]
				?: PubApi.getPackage(lookupString)
				?: return null

		var src: String? = null
		if (pubPackage.homepage != null
				&& pubPackage.homepage.startsWith("https://github.com")) {

			var readmeUrl = "https://raw.githubusercontent.com/${pubPackage.homepage
					.removePrefix("https://github.com/")
					.replace("bloc/", "")
					.replace("tree/", "")}"
			if (!readmeUrl.contains("/master")) {
				readmeUrl += "/master"
			}

			var fileName = "/README.md"
			for (i in 1..2) {
				val response = "$readmeUrl$fileName".httpGet().responseString().third
				if (response.component2() == null) {
					src = response.get()
					break
				} else {
					fileName = fileName.toLowerCase()
					println(fileName)
				}
			}

			if (src != null && src.startsWith("./")) {
				// README.md is referenced in a sub-folder
				readmeUrl += src.replaceFirst(".", "")
				src = readmeUrl.httpGet().responseString().third.get()
			}
			println(readmeUrl + fileName)
		}

		val result = StringBuilder()
		result.append("<html>")
		result.append("<h1>$lookupString</h1>")
		result.append("<small><i>by ${pubPackage.getAuthorName()}</i></small><br><br>")

		result.append("<p>${pubPackage.description}</p><br>")

		if (pubPackage.homepage != null) {
			result.append("<a href=\"${pubPackage.homepage}\">Visit package's homepage</a><br><br>")

			if (pubPackage.homepage.startsWith("https://github.com")) {
				val examplePath: String = if (pubPackage.homepage.contains("/tree/master")) {
					""
				} else {
					"/tree/master"
				}
				result.append("<a href=\"${pubPackage.homepage}$examplePath/example\">Show an example of how to use the package</a><br><br>")
			}
		}

		if (!src.isNullOrEmpty()) {
			val flavour = CommonMarkFlavourDescriptor()
			val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
			val html = HtmlGenerator(src, parsedTree, flavour).generateHtml()

			result.append("<br><h2><u>Documentation</u></h2>")
			result.append(html.replaceFirst("<h1>$lookupString</h1>", ""))
		}
		result.append("</html>")

		return result.toString()
	}
}

class SuggestionElement(private val psiManager: PsiManager, private val element: String) : FakePsiElement() {
	override fun getParent(): PsiElement? = null

	override fun isValid() = true

	override fun getContainingFile(): PsiFile {
		return PsiFileFactory
				.getInstance(project)
				.createFileFromText("hoge.txt", FileTypes.PLAIN_TEXT, "")
	}

	override fun getManager() = psiManager

	override fun getName() = element.split(":")[0]
}
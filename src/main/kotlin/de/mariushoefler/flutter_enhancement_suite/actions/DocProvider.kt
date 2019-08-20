package de.mariushoefler.flutter_enhancement_suite.actions

import com.intellij.openapi.paths.GlobalPathReferenceProvider
import com.intellij.openapi.paths.PathReferenceManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.util.UserDataCache
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vcs.IssueNavigationConfiguration
import com.intellij.patterns.PsiElementPattern
import com.intellij.psi.*
import com.intellij.psi.impl.source.resolve.reference.ArbitraryPlaceUrlReferenceProvider
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ProcessingContext
import com.intellij.util.SmartList
import org.jetbrains.kotlin.idea.completion.returnExpressionItems
import java.util.concurrent.atomic.AtomicReference

class DocProvider : PsiReferenceProvider() {

	override fun getReferencesByElement(element: PsiElement, context: ProcessingContext): Array<PsiReference> {
		return ourRefsCache.get(element, null).value
	}

	companion object {
		private val ourRefsCache = object : UserDataCache<CachedValue<Array<PsiReference>>, PsiElement, Any>("psielement.url.refs") {
			private val myReferenceProvider = AtomicReference<GlobalPathReferenceProvider>()

			override fun compute(element: PsiElement, p: Any): CachedValue<Array<PsiReference>> {
				return CachedValuesManager.getManager(element.project).createCachedValue({
					val navigationConfiguration = IssueNavigationConfiguration.getInstance(element.project) ?:
					return@createCachedValue CachedValueProvider.Result.create<Array<PsiReference>>(PsiReference.EMPTY_ARRAY)

					var refs: MutableList<PsiReference>? = null
					var provider: GlobalPathReferenceProvider? = myReferenceProvider.get()
					val commentText = StringUtil.newBombedCharSequence(element.text, 500)
					for (link in navigationConfiguration!!.findIssueLinks(commentText)) {
						if (refs == null) refs = SmartList()
						if (provider == null) {
							provider = DocUrlProvider()
							myReferenceProvider.lazySet(provider)
						}
						provider.createUrlReference(element, link.targetUrl, link.range, refs)
					}
					val references = refs?.toTypedArray() ?: PsiReference.EMPTY_ARRAY
					CachedValueProvider.Result(references, element, navigationConfiguration)
				}, false)
			}
		}
	}
}

class DocUrlProvider : GlobalPathReferenceProvider() {

	override fun createUrlReference(psiElement: PsiElement, url: String?, rangeInElement: TextRange?, references: MutableList<in PsiReference>): Boolean {
		val myUrl = "https://pub.dev/packages/${psiElement.firstChild.text}"

		return super.createUrlReference(psiElement, myUrl, rangeInElement, references)
	}

}

class UrlReferenceContributor : PsiReferenceContributor() {
	override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {

		registrar.registerReferenceProvider(object : PsiElementPattern.Capture<PsiElement>(PsiElement::class.java) {
			override fun accepts(o: Any?, context: ProcessingContext): Boolean {
				return o is PsiLiteralValue && o.value is String
			}
		}, DocProvider(), PsiReferenceRegistrar.LOWER_PRIORITY)
	}
}
package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.codeHighlighting.HighlightDisplayLevel
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.paths.PsiDynaReference
import com.intellij.openapi.paths.WebReference
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiAnchor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.util.io.HttpRequests
import de.mariushoefler.flutter_enhancement_suite.utils.isPubspecFile
import java.io.IOException
import java.net.UnknownHostException
import java.util.*

class FlutterDocumentationAnnotator : ExternalAnnotator<Array<FlutterDocumentationAnnotator.MyInfo>, Array<FlutterDocumentationAnnotator.MyInfo>>() {

	companion object {
		private const val FETCH_CACHE_TIMEOUT: Long = 10000
		private val EMPTY_ARRAY = arrayOfNulls<WebReference>(0)
	}

	private val myFetchCache = HashMap<String, MyFetchCacheEntry>()
	private val myFetchCacheLock = Any()

	private fun collectWebReferences(file: PsiFile): Array<WebReference> {
		println("collectWebReferences")
		if (file.isPubspecFile()) {
			val pubspecMap = file.firstChild.firstChild.children

			for (entity in pubspecMap) {
				if (entity.text.startsWith("dependencies")) {
					val references = arrayListOf<WebReference>()

					for (element in entity.lastChild.children) {
//						if (element.text.isPubPackageName()) {
						val pubPackage = element.firstChild
						println("Loading documentation for ${pubPackage.text}")
						references.add(WebReference(
								pubPackage,
								pubPackage.textRangeInParent,
								"https://pub.dev/packages/${pubPackage.text}"))
//						}
					}
					val array = arrayOfNulls<WebReference>(references.size)
					println("return")
					return references.toArray(array)
				}
			}
		}
		return arrayOf()
	}

	private fun lookForWebReference(element: PsiElement): WebReference? {
		return lookForWebReference(listOf(*element.references))
	}

	private fun lookForWebReference(references: Collection<PsiReference>): WebReference? {
		for (reference in references) {
			if (reference is WebReference) {
				return reference
			} else if (reference is PsiDynaReference<*>) {
				val webReference = lookForWebReference(reference.references)
				if (webReference != null) {
					return webReference
				}
			}
		}
		return null
	}

	override fun collectInformation(file: PsiFile): Array<MyInfo>? {
		val references = collectWebReferences(file)
//		val infos = arrayOfNulls<MyInfo>(references.size)
		val infos = arrayListOf<MyInfo>()

		for (i in references.indices) {
			val reference = references[i]
			infos.add(MyInfo(PsiAnchor.create(reference.element), reference.rangeInElement, reference.url))
		}

		val array = arrayOfNulls<MyInfo>(references.size)
		return infos.toArray(array)
	}

	override fun doAnnotate(infos: Array<MyInfo>): Array<MyInfo> {
		val fetchResults = arrayOfNulls<MyFetchResult>(infos.size)
		for (i in fetchResults.indices) {
			fetchResults[i] = checkUrl(infos[i].myUrl)
		}

		var containsAvailableHosts = false

		for (fetchResult in fetchResults) {
			if (fetchResult != MyFetchResult.UNKNOWN_HOST) {
				containsAvailableHosts = true
			}
		}

		for (i in fetchResults.indices) {
			val result = fetchResults[i]

			// if all hosts are not available, internet connection may be disabled, so it's better to not report warnings for unknown hosts
			if (result == MyFetchResult.OK || !containsAvailableHosts && result == MyFetchResult.UNKNOWN_HOST) {
				infos[i].myResult = true
			}
		}

		return infos
	}

	override fun apply(file: PsiFile, infos: Array<MyInfo>?, holder: AnnotationHolder) {
		println("apply")

		if (infos == null || infos.isEmpty()) {
			return
		}

		val displayLevel = getHighlightDisplayLevel(file)

		for (info in infos) {
			println(info)

			if (!info.myResult) {
				val element = info.myAnchor.retrieve()
				if (element != null) {
					val start = element.textRange.startOffset
					val range = TextRange(start + info.myRangeInElement.startOffset,
							start + info.myRangeInElement.endOffset)
					val message = getErrorMessage(info.myUrl)

					val annotation = when {
						displayLevel === HighlightDisplayLevel.ERROR -> holder.createErrorAnnotation(range, message)
						displayLevel === HighlightDisplayLevel.WARNING -> holder.createWarningAnnotation(range, message)
						displayLevel === HighlightDisplayLevel.WEAK_WARNING -> holder.createInfoAnnotation(range, message)
						else -> holder.createWarningAnnotation(range, message)
					}

					for (action in getQuickFixes()) {
						annotation.registerFix(action)
					}
				}
			}
		}
	}

	private fun getErrorMessage(url: String): String {
		return "Error when loading $url"
	}

	private fun getQuickFixes(): Array<IntentionAction> {
		return IntentionAction.EMPTY_ARRAY
	}

	private fun getHighlightDisplayLevel(context: PsiElement): HighlightDisplayLevel {
		return HighlightDisplayLevel.WEAK_WARNING
	}

	private fun checkUrl(url: String): MyFetchResult {
		synchronized(myFetchCacheLock) {
			val entry = myFetchCache[url]
			val currentTime = System.currentTimeMillis()

			if (entry != null && currentTime - entry.time < FETCH_CACHE_TIMEOUT) {
				return entry.fetchResult
			}

			val fetchResult = doCheckUrl(url)
			myFetchCache[url] = MyFetchCacheEntry(currentTime, fetchResult)
			return fetchResult
		}
	}

	private fun doCheckUrl(url: String): MyFetchResult {
		if (url.startsWith("mailto")) {
			return MyFetchResult.OK
		}

		println(url)

		try {
			HttpRequests.request(url).connectTimeout(3000).readTimeout(3000).tryConnect()
			println("successful")
		} catch (e: UnknownHostException) {
//			LOG.info(e)
			return MyFetchResult.UNKNOWN_HOST
		} catch (e: HttpRequests.HttpStatusException) {
//			LOG.info(e)
			return MyFetchResult.NONEXISTENCE
		} catch (e: IOException) {
//			LOG.info(e)
		} catch (e: IllegalArgumentException) {
//			LOG.debug(e)
		}

		return MyFetchResult.OK
	}

	private class MyFetchCacheEntry(val time: Long, val fetchResult: MyFetchResult)

	private enum class MyFetchResult {
		OK, UNKNOWN_HOST, NONEXISTENCE
	}


	data class MyInfo(internal val myAnchor: PsiAnchor, internal val myRangeInElement: TextRange, internal val myUrl: String) {

		@Volatile
		internal var myResult: Boolean = false
	}
}

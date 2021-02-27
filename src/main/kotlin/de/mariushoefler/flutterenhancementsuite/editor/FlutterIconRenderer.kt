package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import java.util.Objects
import javax.swing.Icon

class FlutterIconRenderer(private val myIcon: Icon, element: PsiElement) : GutterIconRenderer(), DumbAware {

	private val myId: String = element.text

	override fun getTooltipText() = myId

	override fun getIcon() = myIcon

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other == null || javaClass != other.javaClass) return false

		val renderer: FlutterIconRenderer = other as FlutterIconRenderer
		return Objects.equals(myId, renderer.myId)
	}

	override fun hashCode() = myId.hashCode()
}

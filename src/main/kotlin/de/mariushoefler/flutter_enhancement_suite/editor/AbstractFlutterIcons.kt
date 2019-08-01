package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.openapi.util.IconLoader
import com.jetbrains.rd.util.printlnError
import io.flutter.editor.FlutterEditorAnnotator
import java.io.PrintWriter
import java.util.*
import javax.swing.Icon

open class AbstractFlutterIcons<T>(fileName: String, private val iconClass: Class<T>) {

	private val icons: Properties = Properties()

	init {
		try {
			icons.load(this::class.java.classLoader.getResourceAsStream("/flutter/${fileName}_icons.properties"))
		} catch (e: Exception) {
		}
	}

	fun getIconByCode(code: String): Icon? {
		val iconName = icons.getProperty("$code.codepoint")
		return getIcon(iconName)
	}

	fun getIcon(name: String?): Icon? {
		if (name == null) {
			return null
		}
		val path: String = icons.getProperty(name) ?: return null
		return IconLoader.findIcon(path, iconClass)
	}
}
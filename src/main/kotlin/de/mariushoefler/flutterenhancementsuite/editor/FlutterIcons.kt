package de.mariushoefler.flutter_enhancement_suite.editor

import com.intellij.openapi.util.IconLoader
import java.util.Properties
import javax.swing.Icon

open class FlutterIcons<T>(fileName: String, private val iconClass: Class<T>) {

	private val icons: Properties = Properties()

	init {
		icons.load(this::class.java.classLoader.getResourceAsStream("/flutter/${fileName}_icons.properties"))
	}

	fun getIconByCode(code: String): Icon? {
		val iconName = icons.getProperty("$code.codepoint")
		return getIcon(iconName)
	}

	fun getIcon(name: String?): Icon? {
		name?.let {
			icons.getProperty(it)?.let { path ->
				return IconLoader.findIcon(path, iconClass)
			}
		}
		return null
	}
}

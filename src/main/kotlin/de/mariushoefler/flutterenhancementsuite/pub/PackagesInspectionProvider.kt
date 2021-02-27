package de.mariushoefler.flutterenhancementsuite.pub

import com.intellij.codeInspection.InspectionToolProvider
import com.intellij.codeInspection.LocalInspectionTool

class PackagesInspectionProvider : InspectionToolProvider {
	override fun getInspectionClasses(): Array<Class<out LocalInspectionTool>> {
		return arrayOf(PubPackagesInspection::class.java)
	}
}

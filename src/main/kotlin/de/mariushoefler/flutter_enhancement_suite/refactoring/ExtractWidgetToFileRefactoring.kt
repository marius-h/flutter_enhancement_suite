package de.mariushoefler.flutter_enhancement_suite.refactoring

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.lang.dart.ide.refactoring.ServerRefactoring
import org.dartlang.analysis.server.protocol.ExtractWidgetOptions
import org.dartlang.analysis.server.protocol.RefactoringFeedback
import org.dartlang.analysis.server.protocol.RefactoringKind
import org.dartlang.analysis.server.protocol.RefactoringOptions

class ExtractWidgetToFileRefactoring(private val myProject: Project,
									 private val myFile: VirtualFile,
									 private val offset: Int,
									 private val length: Int) : ServerRefactoring(myProject, "Extract Widget to File", RefactoringKind.EXTRACT_WIDGET, myFile, offset, length) {

	private val options = ExtractWidgetOptions("NewWidget")

	override fun getOptions() = options

	override fun setFeedback(feedback: RefactoringFeedback) {
	}

	public fun setName(name: String): Unit {
		options.name = name
	}

	public fun sendOptions(): Unit {
		setOptions(true, null)
	}
}
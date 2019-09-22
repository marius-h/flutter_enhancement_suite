package de.mariushoefler.flutter_enhancement_suite.editor.linter;

import com.intellij.ide.BrowserUtil
import com.intellij.ide.browsers.BrowserStarter
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBLoadingPanel
import de.mariushoefler.flutter_enhancement_suite.models.LinterRule
import de.mariushoefler.flutter_enhancement_suite.utils.LinterUtils
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.table.AbstractTableModel
import kotlin.concurrent.thread

/**
 * UI for editing rules in "analysis_options.yaml"
 *
 * @since v1.3
 */
class LinterViewPanel(val project: Project, parentDisposable: Disposable) : Disposable, MouseListener {

	lateinit var myContainer: JPanel
	lateinit var myTable: JTable
	lateinit var issueButton: JButton
	var tableModel: MyTableModel

	init {
		println("LinterViewPanel.init")

		Disposer.register(parentDisposable, this)

		LinterUtils.getActiveRules(project)

		issueButton.addMouseListener(this)

		tableModel = MyTableModel()

		rules.forEach {
			if (LinterUtils.activeRules.containsKey(it.name)) {
				it.enabled = true
			}
			tableModel.addRow(it)
		}

		myTable.model = tableModel
		myTable.columnModel.getColumn(0).maxWidth = 32
		myTable.columnModel.getColumn(1).minWidth = 360
		myTable.columnModel.getColumn(2).minWidth = 700
		myTable.autoResizeMode = JTable.AUTO_RESIZE_OFF
	}

	override fun dispose() {
	}

	override fun mousePressed(e: MouseEvent?) {
		BrowserUtil.browse("https://github.com/marius-h/flutter_enhancement_suite/issues/new/choose")
	}

	override fun mouseReleased(e: MouseEvent?) {}

	override fun mouseEntered(e: MouseEvent?) {}

	override fun mouseClicked(e: MouseEvent?) {}

	override fun mouseExited(e: MouseEvent?) {}

	fun getPreferredFocusedComponent() = myTable

	inner class MyTableModel : AbstractTableModel() {
		private val columnNames = arrayListOf("", "Rule", "Description")
		val data = ArrayList<LinterRule>()

		override fun getRowCount(): Int = data.size

		override fun getColumnCount() = columnNames.size

		override fun getValueAt(rowIndex: Int, columnIndex: Int): Any? {
			val rule = data[rowIndex]
			return when (columnIndex) {
				0 -> rule.enabled
				1 -> rule.name
				2 -> rule.description
				else -> null
			}
		}

		override fun getColumnName(column: Int) = columnNames[column]

		override fun getColumnClass(columnIndex: Int): Class<*> {
			return data[0].getAsArray()[columnIndex].javaClass
		}

		fun addRow(rule: LinterRule) {
			data.add(rule)
		}

		override fun isCellEditable(rowIndex: Int, columnIndex: Int): Boolean {
			return columnIndex == 0
		}

		override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {
			if (aValue is Boolean) {
				data[rowIndex].apply {
					enabled = if (aValue) {
						// checks checkbox if rule was added successfully
						LinterUtils.addRule(name, project)
					} else {
						// unchecks checkbox if rule was removed successfully
						!LinterUtils.removeRule(name)
					}
					println("Rule $name - $enabled")
				}
			}
		}
	}
}

package de.mariushoefler.flutterenhancementsuite.codeInsight.hints.types

import com.intellij.codeInsight.hints.ChangeListener
import com.intellij.codeInsight.hints.ImmediateConfigurable
import com.intellij.codeInsight.hints.InlayGroup
import com.intellij.codeInsight.hints.InlayHintsCollector
import com.intellij.codeInsight.hints.InlayHintsProvider
import com.intellij.codeInsight.hints.InlayHintsSink
import com.intellij.codeInsight.hints.SettingsKey
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.ui.dsl.builder.panel
import javax.swing.JPanel

/**
 * This class is responsible for providing type hints in the code.
 *
 * It is used to show the type of a variable.
 * It is also used to show the type of a parameter.
 *
 * @author Marius Höfler
 * @since v1.7.0
 */
@Suppress("UnstableApiUsage")
class DartTypeInlayHintsProvider : InlayHintsProvider<DartTypeInlayHintsProvider.Settings> {
    companion object {
        private val settingsKey = SettingsKey<Settings>("dart.type.hints")
    }

    override fun getCollectorFor(
        file: PsiFile,
        editor: Editor,
        settings: Settings,
        sink: InlayHintsSink
    ): InlayHintsCollector = DartTypeHintsCollector(editor, file, settings)

    override fun createSettings() = Settings()
    data class Settings(var insertBeforeIdentifier: Boolean = true)

    override val name = "Dart Type Hints"

    override val key = settingsKey

    override val group: InlayGroup
        get() = InlayGroup.TYPES_GROUP

    override val previewText = """
        void foo() {
            final bar = "Hello there, General Kenobi!";
        }
    """.trimIndent()

    override fun createConfigurable(settings: Settings): ImmediateConfigurable = object : ImmediateConfigurable {
        val insertTypeHintBeforeIdentifier: Boolean = settings.insertBeforeIdentifier

        override fun createComponent(listener: ChangeListener): JPanel = panel {
            row {
                checkBox("Put type hint before identifier")
                    .applyToComponent {
                        isSelected = insertTypeHintBeforeIdentifier
                        addItemListener {
                            listener.settingsChanged()
                            settings.insertBeforeIdentifier = this@applyToComponent.isSelected
                        }
                    }
            }
        }

        override fun reset() {
            settings.insertBeforeIdentifier = insertTypeHintBeforeIdentifier
            super.reset()
        }

        override val mainCheckboxText: String get() = "Show local variable type hints"
    }
}

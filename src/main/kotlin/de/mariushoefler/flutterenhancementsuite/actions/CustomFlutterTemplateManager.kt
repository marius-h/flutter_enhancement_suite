package de.mariushoefler.flutterenhancementsuite.actions

import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.guessProjectDir
import icons.FlutterIcons
import java.io.File

private const val CUSTOM_TEMPLATES_PATH = ".flutter_file_templates/"
private const val CUSTOM_FILE_TEMPLATE_NAME_PREFIX = "@custom:"
private const val VELOCITY_FILE_TEMPLATE_SUFFIX = ".ft"

/**
 * Custom template manager
 *
 * Allows users to add their own file templates inside "New Flutter Widget" action.
 *
 * @author Karol Czeryna
 * @since v1.6.0
 */
class CustomFlutterTemplateManager {
    fun isCustomFileTemplate(templateName: String): Boolean = templateName.startsWith(CUSTOM_FILE_TEMPLATE_NAME_PREFIX)

    fun appendCustomFileTemplatesTo(builder: CreateFileFromTemplateDialog.Builder, project: Project) =
        findCustomTemplates(project)
            .forEach {
                val customTemplateName = it.name.substringBefore('.')
                val kind = customTemplateName.snakeCaseToTemplateName()
                builder.addKind(kind, FlutterIcons.Flutter, CUSTOM_FILE_TEMPLATE_NAME_PREFIX + customTemplateName)
            }

    fun createCustomFileTemplate(templateName: String, project: Project): FileTemplate? {
        val templateFilename = templateName.removePrefix(CUSTOM_FILE_TEMPLATE_NAME_PREFIX)
        val templateFile = findCustomTemplates(project)
            .firstOrNull { it.name.substringBefore('.') == templateFilename }

        templateFile ?: return null

        return CustomFileTemplate(
            templateFilename,
            computeExtensionFromFileTemplate(templateFile.name),
        ).apply { text = templateFile.readText() }
    }

    private fun findCustomTemplates(project: Project): Array<File> {
        val templatesDir = computeCustomTemplatesDirectory(project)
        return templatesDir?.listFiles() ?: arrayOf()
    }

    private fun computeCustomTemplatesDirectory(project: Project): File? {
        return project.guessProjectDir()
            ?.toNioPath()
            ?.resolve(CUSTOM_TEMPLATES_PATH)
            ?.toFile()
    }

    private fun computeExtensionFromFileTemplate(filename: String) =
        filename.removeSuffix(VELOCITY_FILE_TEMPLATE_SUFFIX).substringAfter('.')

    private fun String.snakeCaseToTemplateName() =
        split('_').joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
}

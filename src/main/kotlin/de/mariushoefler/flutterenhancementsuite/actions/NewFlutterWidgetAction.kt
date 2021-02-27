package de.mariushoefler.flutterenhancementsuite.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.module.ModuleTypeWithWebFeatures
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.search.FileTypeIndex
import com.jetbrains.lang.dart.DartFileType
import com.jetbrains.lang.dart.sdk.DartSdk
import de.mariushoefler.flutterenhancementsuite.utils.toSnakeCase
import icons.FlutterIcons

class NewFlutterWidgetAction :
    CreateFileFromTemplateAction("Flutter Widget", "Create a new Flutter widget", FlutterIcons.Flutter) {
    override fun createFileFromTemplate(name: String?, template: FileTemplate?, dir: PsiDirectory?): PsiFile {
        return super.createFileFromTemplate(name?.toSnakeCase(), template, dir)
    }

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("New Flutter Widget")
            ?.addKind("Stateless widget", FlutterIcons.Flutter, "stateless_widget")
            ?.addKind("Stateful widget", FlutterIcons.Flutter, "stateful_widget")
            ?.addKind("Stateful widget with AnimationController", FlutterIcons.Flutter, "animated_widget")
            ?.addKind("Inherited widget", FlutterIcons.Flutter, "inherited_widget")
    }

    override fun isAvailable(dataContext: DataContext?): Boolean {
        dataContext?.let {
            LangDataKeys.MODULE.getData(dataContext)?.let { module ->
                if (super.isAvailable(dataContext)) {
                    val cond2 =
                        (DartSdk.getDartSdk(module.project) != null && ModuleTypeWithWebFeatures.isAvailable(module))
                    return FileTypeIndex.containsFileOfType(DartFileType.INSTANCE, module.moduleContentScope) || cond2
                }
            }
        }
        return super.isAvailable(dataContext)
    }

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?) = "New Flutter widget"
}

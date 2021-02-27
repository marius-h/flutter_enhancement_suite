import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import de.mariushoefler.flutterenhancementsuite.models.Bloc

object TemplateBuilder {

    enum class Template {
        Event, State, Bloc
    }

    private object Properties {
        const val Name = "NAME"
        const val ProjectName = "PROJECT_NAME"
        const val ClassName = "CLASS_NAME"
    }

    fun build(bloc: Bloc, project: Project, destinationDirectory: PsiDirectory) {
        val manager = FileTemplateManager.getInstance(project)
        val properties = buildProperties(manager.defaultProperties, bloc)

        mapTemplates(bloc).forEach { template ->
            val fileTemplate = manager.getInternalTemplate(template.key.name.toLowerCase())
            FileTemplateUtil.createFromTemplate(fileTemplate, template.value, properties, destinationDirectory)
        }
    }

    private fun mapTemplates(bloc: Bloc) = Template.values().associate {
        when (it) {
            Template.State -> Pair(it, bloc.stateFilename)
            Template.Event -> Pair(it, bloc.eventFilename)
            Template.Bloc -> Pair(it, bloc.blocFilename)
        }
    }

    private fun buildProperties(properties: java.util.Properties, bloc: Bloc) = properties.apply {
        put(Properties.Name, bloc.name)
        put(Properties.ProjectName, bloc.projectName)
        put(Properties.ClassName, bloc.className)
    }
}

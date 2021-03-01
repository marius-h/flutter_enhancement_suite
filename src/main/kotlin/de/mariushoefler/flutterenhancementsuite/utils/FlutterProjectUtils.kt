package de.mariushoefler.flutterenhancementsuite.utils

import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import io.flutter.pub.PubRoot
import io.flutter.sdk.FlutterSdk
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.SafeConstructor
import org.yaml.snakeyaml.nodes.Tag
import org.yaml.snakeyaml.representer.Representer
import org.yaml.snakeyaml.resolver.Resolver
import java.io.File
import java.io.IOException

object FlutterProjectUtils {

    fun readProjectName(project: Project): String? {
        val pubspec = VfsUtil.findFileByIoFile(File("${project.basePath}/pubspec.yaml"), true) ?: return null
        val properties = readPubspecFileToMap(pubspec)
        return properties?.get("name") as String
    }

    @Throws(IOException::class)
    fun readPubspecFileToMap(pubspec: VirtualFile): Map<String, Any>? {
        val contents = String(pubspec.contentsToByteArray(true))
        return loadPubspecInfo(contents)
    }

    private fun loadPubspecInfo(yamlContents: String): Map<String, Any>? {
        val yaml = Yaml(
            SafeConstructor(),
            Representer(),
            DumperOptions(),
            object : Resolver() {
                override fun addImplicitResolvers() {
                    this.addImplicitResolver(Tag.BOOL, BOOL, "yYnNtTfFoO")
                    this.addImplicitResolver(Tag.NULL, NULL, "~nN\u0000")
                    this.addImplicitResolver(Tag.NULL, EMPTY, null)
                    this.addImplicitResolver(Tag("tag:yaml.org,2002:value"), VALUE, "=")
                    this.addImplicitResolver(Tag.MERGE, MERGE, "<")
                }
            }
        )

        return yaml.load<Map<String, Any>>(yamlContents)
    }

    /**
     * Runs `flutter pub get` in project
     *
     * @since v1.2
     */
    fun runPackagesGet(file: VirtualFile?) {
        PubRoot.forDirectory(file?.parent)?.let { pubRoot ->
            ProjectLocator.getInstance().guessProjectForFile(file)?.let { project ->
                FileDocumentManager.getInstance().saveAllDocuments()
                val module = pubRoot.getModule(project)
                if (module != null) {
                    FlutterSdk.getFlutterSdk(project)?.flutterPackagesGet(pubRoot)
                        ?.startInModuleConsole(module, { pubRoot.refresh() }, null)
                }
            }
        }
    }
}

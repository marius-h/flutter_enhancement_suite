package pubassist
import com.intellij.codeInspection.InspectionToolProvider

class PackagesInspectionProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> {
        return arrayOf(PubPackagesInspection::class.java)
    }
}
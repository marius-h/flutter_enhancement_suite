package de.mariushoefler.flutterenhancementsuite.editor.icons

import de.mariushoefler.flutterenhancementsuite.editor.FlutterIcons
import de.mariushoefler.flutterenhancementsuite.utils.toSnakeCase
import javax.swing.Icon

object MdiIcons : FlutterIcons<MdiIcons>("material_community", MdiIcons::class.java) {

    override fun getIcon(name: String?): Icon? {
        return super.getIcon(name?.toSnakeCase())
    }
}

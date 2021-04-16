package eu.glatz.imagej

import ij.IJ
import ij.Macro
import ij.plugin.FolderOpener
import ij.plugin.PlugIn
import java.io.File

class StackLoaderPlugin : PlugIn {
    override fun run(args: String) {

        var path = ""

        if (args.isNullOrBlank() || args.split(" ").isEmpty()) {
            IJ.log("No args $args ${Macro.getOptions()}")
            if (Macro.getOptions() != null && Macro.getOptions().split(" ").isNotEmpty()) {
                IJ.log("Args ${Macro.getOptions()}")
                Macro.getOptions().split(" ").forEach {
                    if (it.startsWith("-f="))
                        path = it.substringAfter("-f=").replace("\"", "").replace("[", "").replace("]", "")
                }
            } else {
                IJ.log("!! ${Macro.getOptions()}")
            }
        } else {
            IJ.log("Args: ${args}")
            args.split(" ").forEach {
                path = it.substringAfter("-f=").replace("\"", "").replace("[", "").replace("]", "")
            }
        }

        IJ.log("!!: ${path}")

        val imageStack = FolderOpener.open(File(path).path)
        imageStack.show()
    }
}

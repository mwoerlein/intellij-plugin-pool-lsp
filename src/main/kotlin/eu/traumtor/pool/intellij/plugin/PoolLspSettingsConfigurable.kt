package eu.traumtor.pool.intellij.plugin

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.not

class PoolLspSettingsConfigurable (val project: Project) : BoundSearchableConfigurable("Pool LSP Settings","Pool") {
    private val settings = PoolLspSettings.getInstance(project)

    override fun createPanel(): DialogPanel {
        return panel {
            lateinit var enablePoolLspCheckBox: Cell<JBCheckBox>
            lateinit var useProjectSourceRootsCheckBox: Cell<JBCheckBox>

            row {
                enablePoolLspCheckBox = checkBox("Enable Pool LSP")
                    .bindSelected(settings::enabled)
            }
            indent {
                row("Pool LSP executable:") {
                    textFieldWithBrowseButton(FileChooserDescriptor(true, false, false, false, false, false), project)
                        .align(AlignX.FILL)
                        .bindText(settings::command)
                }
            }.enabledIf(enablePoolLspCheckBox.selected)

            row {
                useProjectSourceRootsCheckBox = checkBox("Use project source folders")
                    .bindSelected(settings::useProjectSourceRoots)
            }.enabledIf(enablePoolLspCheckBox.selected)
            indent {
                // TODO: handle multiple source paths
                row ("Pool source path:") {
                    textFieldWithBrowseButton(FileChooserDescriptor(false, true, false, false, false, false), project)
                        .align(AlignX.FILL)

                        .bindText(settings::firstSourcePath)
                }
            }.enabledIf(enablePoolLspCheckBox.selected)
                .visibleIf(!useProjectSourceRootsCheckBox.selected)
        }.apply { restartPoolLspServer(project) }
    }

    override fun getDisplayName() = "Pool"
}
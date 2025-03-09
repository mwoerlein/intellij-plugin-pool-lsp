package eu.traumtor.pool.intellij.plugin

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.lsp.api.LspServer
import com.intellij.platform.lsp.api.LspServerManager
import com.intellij.platform.lsp.api.LspServerSupportProvider
import com.intellij.platform.lsp.api.ProjectWideLspServerDescriptor
import com.intellij.platform.lsp.api.lsWidget.LspServerWidgetItem
import com.intellij.ui.IconManager

@Suppress("UnstableApiUsage")
internal class PoolLspServerSupportProvider : LspServerSupportProvider {
    override fun fileOpened(project: Project, file: VirtualFile, serverStarter: LspServerSupportProvider.LspServerStarter) {
        val descriptor = PoolLspServerDescriptor(project)
        if (descriptor.isSupportedFile(file)) {
            serverStarter.ensureServerStarted(descriptor)
        }
    }
    override fun createLspServerWidgetItem(lspServer: LspServer, currentFile: VirtualFile?) =
        LspServerWidgetItem(
            lspServer,
            currentFile,
            IconManager.getInstance()
                .loadRasterizedIcon("icons/pool.svg", PoolLspServerSupportProvider::class.java.classLoader, 199994656, 2),
            settingsPageClass = PoolLspSettingsConfigurable::class.java
        )
}

@Suppress("UnstableApiUsage")
private class PoolLspServerDescriptor(project: Project) : ProjectWideLspServerDescriptor(project, "POOL-LS") {
    private val settings = PoolLspSettings.getInstance(project)

    override fun isSupportedFile(file: VirtualFile): Boolean {
        return settings::enabled.get() && file.extension == "pool"
    }
    override fun createCommandLine(): GeneralCommandLine {
        val gcl = GeneralCommandLine(settings::command.get())
        if (settings::useProjectSourceRoots.get()) {
            ModuleManager.getInstance(project).modules.forEach { module ->
                ModuleRootManager.getInstance(module).sourceRootUrls.forEach { url ->
                    gcl.addParameters("--sourceUri", url)
                }
            }
        } else {
            settings::sourcePaths.get().forEach { dir ->
                gcl.addParameters("--sourceUri", "file://$dir")
            }
        }
        return gcl
    }
}

@Suppress("UnstableApiUsage")
fun restartPoolLspServer(project: Project) {
    LspServerManager.getInstance(project).stopAndRestartIfNeeded(PoolLspServerSupportProvider::class.java)
}

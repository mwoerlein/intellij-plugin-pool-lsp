package eu.traumtor.pool.intellij.plugin

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "PoolLspSettings", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class PoolLspSettings (val project: Project) : SimplePersistentStateComponent<PoolLspState>(PoolLspState()) {
    var enabled
        get() = state.enabled
        set(value) {
            state.enabled = value
        }

    var command: String
        get() = state.command ?: "pool-language-server"
        set(value) {
            state.command = value
        }

    var useProjectSourceRoots
        get() = state.useProjectSourceRoots
        set(value) {
            state.useProjectSourceRoots = value
        }

    // TODO: handle multiple source paths
    var firstSourcePath
        get() = state.sourcePaths.firstOrNull() ?: ""
        set(value) {
            state.sourcePaths.clear();
            if (value != null && value.isNotEmpty()) {
                state.sourcePaths.add(value)
            }
        }

    var sourcePaths
        get() = state.sourcePaths
        set(value) {
            state.sourcePaths = value
        }

    companion object {
        fun getInstance(project: Project): PoolLspSettings = project.service()
    }
}

class PoolLspState : BaseState() {
    var enabled by property(true)
    var command by string("pool-language-server")
    var useProjectSourceRoots by property(true)
    var sourcePaths by stringSet()
}

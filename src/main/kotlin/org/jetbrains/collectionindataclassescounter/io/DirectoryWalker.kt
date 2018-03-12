package org.jetbrains.collectionindataclassescounter.io

import java.io.File

class DirectoryWalker(private val dirPath: String, private val maxDepth: Int = Int.MAX_VALUE) {
    private fun walkDirectory(callback: (File) -> Unit) {
        val dir = File(dirPath)
        dir.walkTopDown().maxDepth(maxDepth).forEach {
            if (it.isFile && it.extension == "kt") {
                callback(it)
            }
        }
    }

    fun run(callback: (File) -> Unit) {
        walkDirectory { file: File -> callback(file) }
    }
}
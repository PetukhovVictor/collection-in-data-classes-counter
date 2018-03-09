package org.jetbrains.collectionindataclassescounter

import com.xenomachina.argparser.ArgParser

fun main(args : Array<String>) {
    val parser = ArgParser(args)
    val sourcesDirectory by parser.storing("-i", "--input", help="path to folder with Kotlin source code files")

    Runner.run(sourcesDirectory)
}
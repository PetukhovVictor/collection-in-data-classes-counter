package org.jetbrains.collectionindataclassescounter

import com.fasterxml.jackson.core.type.TypeReference
import org.jetbrains.collectionindataclassescounter.helpers.TimeLogger
import org.jetbrains.collectionindataclassescounter.io.DirectoryWalker
import org.jetbrains.collectionindataclassescounter.io.FileWriter
import org.jetbrains.collectionindataclassescounter.io.JsonFilesReader
import org.jetbrains.collectionindataclassescounter.structures.CollectionsByDataClassesStatistic
import java.io.File
import java.nio.file.Files
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

object Runner {
    private const val dataClassRegex = "data class .+?(?=\\()(?=((?:(?=.*?\\((?!.*?\\2)(.*\\)(?!.*\\3).*))(?=.*?\\)(?!.*?\\3)(.*)).)+?.*?(?=\\2)[^(]*(?=\\3\$)))"
    private const val multiLineCommentRegex = "/\\*.*\\*/"
    private const val singleLineCommentRegex = "//.*(?=\\n)"
    private const val stringsRegex = "/\".*\"/"
    private const val multilineStringsRegex = "/\"\"\".*\"\"\"/"
    private const val dataClassFieldsRegex = "(?:val|var).+?:\\s*(?<collectionType>%s)"

    private const val collectionTypesFilename = "./collectionTypes.json"
    private const val resultFilename = "./result.json"

    private fun sourceCodeMatch(sourceCodeOriginal: String, collectionTypes: ArrayList<String>, statistic: CollectionsByDataClassesStatistic) {
        // get source code and remove comments and strings
        val sourceCode = sourceCodeOriginal
                .replace(multiLineCommentRegex.toRegex(RegexOption.DOT_MATCHES_ALL), "")
                .replace(singleLineCommentRegex.toRegex(), "")
                .replace(multilineStringsRegex.toRegex(RegexOption.DOT_MATCHES_ALL), "")
                .replace(stringsRegex.toRegex(), "")

        // balancing group recursive parsing. See https://stackoverflow.com/a/47162099
        val patternClass = Pattern.compile(dataClassRegex, DOTALL)
        val matcherClass = patternClass.matcher(sourceCode)

        while (matcherClass.find()) {
            val fields = matcherClass.group(1)
            val collectionIdentifierGroups = collectionTypes.joinToString("|")
            val patternFields = Pattern.compile(dataClassFieldsRegex.format(collectionIdentifierGroups), DOTALL)
            val matcherFields = patternFields.matcher(fields)
            var isMarkClass = false

            while (matcherFields.find()) {
                val collectionType = matcherFields.group("collectionType")

                if (!isMarkClass) {
                    statistic.dataClassesWithCollections++
                    isMarkClass = true
                }
                if (statistic.collectionsStatistic.contains(collectionType)) {
                    statistic.collectionsStatistic[collectionType] = statistic.collectionsStatistic[collectionType] !!+ 1
                } else {
                    statistic.collectionsStatistic[collectionType] = 1
                }
            }

            statistic.dataClassesTotal++
        }
    }

    fun run(sourcesDirectory: String) {
        if (!Files.exists(File(collectionTypesFilename).toPath())) {
            println("COLLECTION TYPES FILE NOT FOUND (you must put collectionTypes.json file in current directory)")
            return
        }

        val timeLogger = TimeLogger(task_name = "Collections in data class counting")
        val statistic = CollectionsByDataClassesStatistic()
        val collectionTypesReference = object: TypeReference<ArrayList<String>>() {}
        val collectionTypes = JsonFilesReader.readFile<ArrayList<String>>(collectionTypesFilename, collectionTypesReference)

        DirectoryWalker(sourcesDirectory).run {
            val timeLoggerFile = TimeLogger(task_name = "File $it processing")
            sourceCodeMatch(it.readText(), collectionTypes, statistic)
            timeLoggerFile.finish()
        }

        FileWriter.write(resultFilename, statistic)

        timeLogger.finish(fullFinish = true)
    }
}
package org.jetbrains.collectionindataclassescounter

import org.jetbrains.collectionindataclassescounter.helpers.TimeLogger
import org.jetbrains.collectionindataclassescounter.io.DirectoryWalker
import org.jetbrains.collectionindataclassescounter.io.FileWriter
import org.jetbrains.collectionindataclassescounter.structures.CollectionsByDataClassesStatistic
import java.util.regex.Pattern
import java.util.regex.Pattern.DOTALL

object Runner {
    private val collectionIdentifiers = setOf(
            "AbstractCollection",
            "AbstractIterator",
            "AbstractList",
            "AbstractMap",
            "AbstractMutableCollection",
            "AbstractMutableList",
            "AbstractMutableMap",
            "AbstractMutableSet",
            "AbstractSet",
            "ArrayList",
            "BooleanIterator",
            "ByteIterator",
            "CharIterator",
            "Collection",
            "DoubleIterator",
            "FloatIterator",
            "Grouping",
            "HashMap",
            "HashSet",
            "IndexedValue",
            "IntIterator",
            "Iterable",
            "Iterator",
            "LinkedHashMap",
            "LinkedHashSet",
            "List",
            "ListIterator",
            "LongIterator",
            "Map",
            "MutableCollection",
            "MutableIterable",
            "MutableIterator",
            "MutableList",
            "MutableListIterator",
            "MutableMap",
            "MutableSet",
            "RandomAccess",
            "Set",
            "ShortIterator",
            "SortedSet",
            "NavigableSet",
            "Queue",
            "BlockingQueue",
            "TransferQueue",
            "Deque",
            "BlockingDeque",
            "SortedMap",
            "NavigableMap",
            "ConcurrentMap",
            "ConcurrentNavigableMap",
            "BlockingQueue",
            "TransferQueue",
            "BlockingDeque",
            "ConcurrentMap",
            "ConcurrentNavigableMap",
            "LinkedBlockingQueue",
            "ArrayBlockingQueue",
            "PriorityBlockingQueue",
            "DelayQueue",
            "SynchronousQueue",
            "LinkedBlockingDeque",
            "LinkedTransferQueue",
            "CopyOnWriteArrayList",
            "CopyOnWriteArraySet",
            "ConcurrentSkipListSet",
            "ConcurrentHashMap",
            "ConcurrentSkipListMap",
            "AbstractQueue",
            "AbstractSequentialList",
            "ArrayDeque",
            "AttributeList",
            "ConcurrentLinkedDeque",
            "ConcurrentHashMap",
            "ConcurrentLinkedQueue",
            "ConcurrentSkipListSet",
            "EnumSet",
            "LinkedList",
            "PriorityQueue",
            "RoleList",
            "RoleUnresolvedList",
            "Stack",
            "SynchronousQueue",
            "TreeSet",
            "Vector",
            "Arrays",
            "TreeMap",
            "WeakHashMap",
            "IdentityHashMap",
            "Dictionary",
            "Hashtable",
            "Properties",
            "BitSet",
            "Enumeration",
            "ConcurrentMap",
            "Comparable",
            "Comparator"
    )

    private const val dataClassRegex = "data class .+?(?=\\()(?=((?:(?=.*?\\((?!.*?\\2)(.*\\)(?!.*\\3).*))(?=.*?\\)(?!.*?\\3)(.*)).)+?.*?(?=\\2)[^(]*(?=\\3\$)))"
    private const val multiLineCommentRegex = "/\\*.*\\*/"
    private const val singleLineCommentRegex = "//.*(?=\\n)"
    private const val stringsRegex = "/\".*\"/"
    private const val dataClassFieldsRegex = "(?:val|var).+?:\\s*(?<collectionType>%s)"

    fun run(sourcesDirectory: String) {
        val timeLogger = TimeLogger(task_name = "Collections in data class counting")
        val statistic = CollectionsByDataClassesStatistic()

        DirectoryWalker(sourcesDirectory).run {
            val timeLoggerFile = TimeLogger(task_name = "File $it processing")
            // get source code and remove comments and strings
            val sourceCode = it.readText()
                    .replace(multiLineCommentRegex.toRegex(RegexOption.DOT_MATCHES_ALL), "")
                    .replace(singleLineCommentRegex.toRegex(), "")
                    .replace(stringsRegex.toRegex(RegexOption.DOT_MATCHES_ALL), "")

            // balancing group recursive parsing. See https://stackoverflow.com/a/47162099
            val patternClass = Pattern.compile(dataClassRegex, DOTALL)
            val matcherClass = patternClass.matcher(sourceCode)

            while (matcherClass.find()) {
                val fields = matcherClass.group(1)
                val collectionIdentifierGroups = collectionIdentifiers.joinToString("|")
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

            timeLoggerFile.finish()
        }

        FileWriter.write("./result.json", statistic)

        timeLogger.finish(fullFinish = true)
    }
}
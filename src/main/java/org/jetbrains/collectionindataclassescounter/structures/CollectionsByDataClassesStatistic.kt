package org.jetbrains.collectionindataclassescounter.structures

typealias CollectionsStatistic = MutableMap<String, Int>

data class CollectionsByDataClassesStatistic(
        var dataClassesTotal: Int = 0,
        var dataClassesWithCollections: Int = 0,
        val collectionsStatistic: CollectionsStatistic = mutableMapOf()
)
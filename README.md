# collection-in-data-classes-counter
Counting usage fields-collection statistic in Kotlin data classes

## Program use

The program runs with the specified directory with the Kotlin source code files, among which you need to count the statistics of the use of fields-collection in the data classes.

At the output, the program will generate `result.json` inside which will be written number of data classes, number of data classes with fields-collection and fields-collection statistic by type.
Example:
```
{
   "dataClassesTotal":4712,
   "dataClassesWithCollections":976,
   "collectionsStatistic":{
      "Map":174,
      "List":888,
      "ArrayList":41,
      "Set":111,
      "MutableList":62,
      "Collection":31,
      "Stack":2,
      "MutableMap":28,
      "LinkedList":6,
      "HashSet":4,
      "LinkedHashSet":1,
      "SortedSet":2,
      "TreeSet":2,
      "HashMap":6,
      "LinkedHashMap":4,
      "SortedMap":3,
      "TreeMap":4,
      "MutableSet":8,
      "Iterator":1,
      "Iterable":3,
      "Queue":1,
      "NavigableMap":2,
      "MutableCollection":1,
      "Properties":2,
      "AbstractMap":2,
      "Dictionary":1,
      "Hashtable":1,
      "WeakHashMap":1,
      "Vector":1
   }
}
```

Also you must put collection types file in current directory (collectionTypes.json). [Example](https://github.com/PetukhovVictor/collection-in-data-classes-counter/blob/master/collectionTypes.json).

### Program arguments

* `-i`, `--input`: path to folder with Kotlin source code files;

### How to run

To run program you must run `main` function in `main.kt`, not forgetting to set the program arguments.

Also you run jar downloading it from the [release files](https://github.com/PetukhovVictor/collection-in-data-classes-counter/releases).

#### Example of use

```
java -jar collection-in-data-classes-counter-0.1.jar -i ./source_codes
```

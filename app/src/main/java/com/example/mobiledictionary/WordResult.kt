//package com.example.mobiledictionary
//
//data class WordResult(
//    val word: String,
//    val phonetic: String?,
//    val meanings: List<Meaning>,
//
//    )
//
//data class Meaning(
//    val partOfSpeech: String,
//    val definitions: List<Definition>,
//    val synonyms: List<String>,
//    val antonyms: List<String>,
//)
//
//data class Definition(
//    val definition: String,
//)

package com.example.mobiledictionary

data class WordResult(
    val word: String,
    val phonetics: List<Phonetic>,
    val meanings: List<Meaning>
)

data class Phonetic(
    val text: String?,
    val audio: String?
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
    val synonyms: List<String>,
    val antonyms: List<String>
)

data class Definition(
    val definition: String
)

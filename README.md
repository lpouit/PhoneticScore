# PhoneticScore
----------

Enhancement and creation of rules are welcomed, create a [/pulls](Pull Request).

----------

## Currently availables languages
See [/phonetics]
|LANGUAGE|ISO-CODE|LEVELS|
|--------|--------|------|
|French  |fra     |3     |

----------

## Examples
```java
// The new instance will be created for the specified language.
// The rules of phonetic transcription are automatically loaded.
// This example is in French.
PhoneticScore ph = PhoneticScore.getInstance("fra");

// This function takes in count the word order.
// Any unwanted characters will be removed, see PhoneticScore#normalisation(String)
phon.similarityScore("Un ours casse un oeuf avec sa patte.", "Une aourse quasse un ouf avec sa pâte.");
// Returns 0.7894736842105263

// This function ignores the word order.
// Any unwanted characters will be removed, see PhoneticScore#normalisation(String)
phon.similarityScoreByWord("Un ours casse un oeuf avec sa patte.", "Une aourse quasse un ouf avec sa pâte.");
// Returns 0.7625

phon.similarityScore("Un ours casse un oeuf avec sa patte.", "Un ouf ai quassé par la pâte d'une aourse.");
// Returns 0.3333333333333333

phon.similarityScoreByWord("Un ours casse un oeuf avec sa patte.", "Un ouf ai quassé par la pâte d'une aourse.");
// Returns 0.041666666666666664

// To clear all the loaded instances from memory.
PhoneticScore.resetInstances();
```
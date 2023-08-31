# PhoneticScore
----------
This library is a toolbox with several ways to evaluate the similarity of words and texts.
The first approach applies the Levenshtein distance on text normalised without accent.
The second approach applies the Levenshtein distance on text transcripted in phonetics.
For each one, you can score a whole sentence/text, or score it word by word independtly of the order.

Enhancement and creation of phonetics rules are welcomed, create a [Pull Request](https://github.com/lpouit/PhoneticScore/pulls)
----------

## Currently availables languages
Phonetic rules are stored as RegEx in [/phonetics](/phonetics)
|LANGUAGE|ISO-CODE|LEVELS|
|--------|--------|------|
|French  |fra     |3     |
|German  |deu     |3     |

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

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

// One instance of LevenshteinScore is created at all.
LevenshteinScore leven = PhoneticScore.getInstance();

// A new instance of PhoneticScore is created for each the specified language.
// The rules of phonetic transcription are automatically loaded.
// This example is in French.
PhoneticScore phon = PhoneticScore.getInstance("fra");

// example of transcription of a sentence in French to phonetics
phon.getPhonetic("Le jeune d'Artagnan s'éprend de l'épouse de son propriétaire, Constance Bonacieux, lingère de la reine Anne d'Autriche.", Level);
// the result depends on the level
// level 1: lə ʒøn dartaɲɑ̃ seprɑ̃ də lepuz də sɔ proprjetɛr, kɔstɑ̃s bonasjø, lɛ̃ʒɛr də la rɛn an dotriʃ.
// level 2: lə ʒøn dartaɲɑ̃ seprɑ̃ də lepuz də sɔ proprietɛr, kɔstɑ̃s bonasiø, lɛ̃ʒɛr də la rɛn an dotriʃ.
// level 3: l ʒn drtɲ spr d lpz d s prprtr, ksts bns, lʒr d l rn an dtrʃ.

// examples of similarity-scores 
String s1="Un ours casse un oeuf avec sa patte."
String s2="Une ourse kasse un euf avec sa pâte."
// phonetic transcriptions are
// ɛ̃ urs kas ɛ̃ øf avɛk sa pat.
// yn urs kas ɛ̃ øf avɛk sa pat.

// score using only Levenshtein distance on the whole sentence, score=0.86
leven.similarityScore(s1, s2);

// score using Levenshtein distance on each word, score=0.85
leven.similarityScoreByWord(s1, s2);

// score using phonetics level 2 and Levenshtein distance on the whole sentence, score=0.93
phon.similarityScore(s1, s2);

// score using phonetics level 2 and Levenshtein distance on each word, score=0.93
phon.similarityScoreByWord(s1, s2);

// To clear all the loaded instances from memory.
PhoneticScore.resetInstances();
```

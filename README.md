OnAIR-nlp
=========

An NLP tool that will be used by [OnAIR](http://ccsl.ime.usp.br/pt-br/onair) project. It reads an input file formatted such as each line represents a sentence and outputs its analysis.

The core NLP library used is [Apache OpenNLP](http://opennlp.apache.org), wrapped by [CoGrOO](http://cogroo.sourceforge.net) API, as well as some CoGrOO tools, like the Featurizer and Lemmatizer. English models are provided by OpenNLP, while Portuguese models are provided by CoGrOO.

# Get it

Download onair-nlp.jar from the download panel.

# Usage

```
Usage: java -jar onair-nlp.jar lang inputFile outputFile encoding
  supported languages: pt en
```

# Example

To analyze a UTF-8 English document: `java -jar onair-nlp.jar en in.txt out.txt UTF-8`

To analyze a UTF-8 Portuguese document: `java -jar onair-nlp.jar pt in.txt out.txt UTF-8`

# Sample output

Column 1: lexeme

Column 2: lemma (Portuguse only)

Column 3: POS tag

Column 4: Features (Portuguse only)

Column 5: Chunks

```
Fantasia        [fantasia]   n      F=S        B-NP  
é               [ser]        v-fin  PR=3S=IND  B-VP  
o               [o]          art    M=S        B-NP  
nome            [nome]       n      M=S        I-NP  
dado            [dar]        v-pcp  M=S        B-VP  
a               [a]          prp    -          B-PP  
diversos        [diverso]    adj    M=P        B-NP  
espetáculos     [espetáculo] n      M=P        I-NP  
equestres       [equestre]   adj    M=P        I-NP  
tradicionais    [tradicional] adj    M=P        I-NP  
que             [que]        pron-indp M=P        B-NP  
simulam         [simular]    v-fin  PR=3P=IND  B-VP  
assaltos        [assalto]    n      M=P        B-NP  
militares       [militar]    adj    M=P        I-NP
```

# Tag set

## Portuguese

The tagset is derived from [Floresta tag set](http://beta.visl.sdu.dk/visl/pt/symbolset-floresta.html) Check the sections 2.1 (Group forms) and 2.3 (Word classes).

## English
[Penn Treebank tag set](http://www.ims.uni-stuttgart.de/projekte/CorpusWorkbench/CQP-HTMLDemo/PennTreebankTS.html)
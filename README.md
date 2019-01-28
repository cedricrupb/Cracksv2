# Cracks FactChecker (version 2.0)

## Approach
The aim of Fact Checking is to confirm or reject a given statement.
As an example, a Fact Checker should return a higher confidence for 
```
Alfonso XIII of Spain's birth place is Madrid.
```
than for 
```
Alfonso XIII of Spain's birth place is France.
```
Existing techniques utilizes a plethora of trained classifier and statistics.
Although individual approaches differ from each other, they seem to share a feature space build
from similarity scores between query and answer candidates. In contrast, our approach Cracks evaluates query statements in more 
direct way. 

Cracks mainly consists of the following steps:
+ Classify query term Q, search term S and intention I of a given statement
+ Search in the corpus (for now only Wikipedia) for all information regarding Q.
+ Filter candidate sentence C if the contain the search term S
+ Evaluate if the intention I is fulfilled for at least one candidate in C

To evaluate a given statements, Cracks employs a predefined rule set. A single rule looks like 
```
[Person*] birth place is [Place].#birth_place
```
Applying this rule to the example from the beginning, we retrieve to entities "Alfonso XIII of Spain"
and "Madrid" while annotating "Alfonso XIII of Spain" as the query term Q. More importantly, 
we retrieve the intention I of statement. In this case, we ask for the birth place.

To search in the corpus, we directly query Wikipedia for Q.
As the query terms in our setup resemble Wikipedia title, we can query the endpoint directly
with Q. After retrieving the content of a matching Wikipedia page,
we check in every sentence if the search term S occurs. If the term occurs, 
we will extend our candidate set. Otherwise, the sentence will be omitted.

At this stage, we assume that the candidates are related to Q and contain the search term S.
This reduce the fact checking task only classifying the intention.

To classify the intention, we trained a Naive Bayes Classifier NBC while considering only a window of 7 words around the search term.
By cleaning the feature space, NBC reaches a high performance classfying the intention 
of a sentence (see Gerbil evaluation).

Some insights:
+ Step 1-3: Can be applied uniformly for training and test set
+ Step 4 Training: We retrieve multiple candidates per statement. Some candidates are plain noise to the classifier. Therefore, we bootstrap the learning process. 
  In other words, we select the candidate that contains a high frequent word in the intention domain.
  Example: For birth_place, a sentence containing "bear" is more likely to be picked since we see "bear" frequently for this intention.
+ Step 4 Testing: We again retrieve multiple candidates. Therefore we select the candidate for which NBC is most confident.
  In some cases, we retrieve no candidates than the statement will be evaluated as false.


## Build and Execute

### Build
To build the project, it is necessary to clone the project.
In the project folder, you have to run the following commands.
```
$ cd cracks/
$ mvn clean install
```
After this step, a runnable jar together with all libs will be placed 
in the target folder. You can move this jar freely. However, the libs folder have to be in the same directory.

(Optional): You may have noticed the cache folder in the project root.
If you move this folder to the directory of your jar, you can save time while executing.
Cracks will load from cache instead from a Wikipedia endpoint.

### Execute
This step requires that you finish the Build process. We assume you are in the folder of your jar
 (for this instruction we call the jar: cracks-2.0.jar).
 
 First we want to train a classifier:
 ```
 $ java -jar cracks-2.0.jar train <TRAIN_FILE.tsv> model.json
 ```
This command load a TSV file and use all entities for training.
After training, the trained classifier will be saved to "model.json".
Obviously, you could choose another name instead of "model.json".
If you want to change the TSV encoding or the classifier type, have a look at
the command line help.
```
 $ java -jar cracks-2.0.jar -h train
 ```
 
 Now want to see the power of our classifier:
 ```
  $ java -jar cracks-2.0.jar predict model.json <TEST_FILE.tsv> <RESULT_FILE.ttl> 
  ```
  Let model.json be our trained model from the previous step. By employing a unlabelled dataset TEST_FILE.tsv,
  the classifier will predict for each statement if it is true or not.
  The result can be found in RESULT_FILE.ttl. The result file follows the expected format of [Gerbil](http://aksw.org/Projects/GERBIL.html).
  
  
 ## Team
 The main contributor to this project are Cedric Richter and André Sonntag.
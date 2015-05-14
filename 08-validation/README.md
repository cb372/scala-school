# Validating input

This week I'd like to try some group design/coding in Scala.

We'll work on a feature of the new Quiz API that the CAPI team are currently building.

## Problem statement

The quiz API will allow editorial staff to create quizzes (via a UI tool). The API has an endpoint that accepts some json in a POST body, deserializes a Quiz object from that json, and saves it to a datastore.

The json looks something like this:

```
{
  "format": "MultipleChoice",
  "title": "My amazing quiz",
  "questions": [{
    "title": "Question 1",
    "choices": [{
      "title": "Choice 1 for Question 1",
      "correct": true
    }, {
      "title": "Choice 2 for Question 1"
    }, {
      ...
    }]
  }, {
    ... more questions ...
  }],
  "verdictRules": [{
    "condition": {
      "minScore": 0,
      "maxScore": 3
    },
    "template": {
      "titleTemplate": "Rubbish! You only got $score"
    }
  }, {
    ... more verdict rules ...
  }]
}
```

`play-json` takes care of validating the json, i.e. checking that the json object has the appropriate fields to allow a Quiz to be deserialized from it. But we also need to validate the input against some business rules, e.g.:

* A multiple choice quiz must have at least one correct choice for each answer
* A quiz must have at least one question
* The score ranges for verdict rules must not overlap

## Requirements

* If a quiz POSTed to the endpoint is valid, it should be saved to the datastore and the API should reply with a 200 OK response containing the quiz's ID
* If the quiz is invalid, it should reply with a 400 Bad Request reponse containing a list of error messages
* There are 2 formats of quiz: MultipleChoice and Personality. Some validation rules apply to all quizzes, but some validation is format-specific. More formats may be added in the future.

## Things to consider

* Should the validation logic be called by the Play controller? Or by the repository? Or should we add a new layer in between?
* Should the validation rules be implemented as `object`s? Or functions? Or something else?
* Testability should be a key design principle here. There are no dependencies on anything, and we're just applying business rules to objects, so this should be easy to unit test.
* Could [Scalaz Validation](http://eed3si9n.com/learning-scalaz/Validation.html) be useful here? Or is it just as easy to roll our own solution?
* There is no right answer!

## The code

The GitHub repo is here: https://github.com/guardian/quizzical

Take a look at the `validateAndPersistNewQuiz` method in https://github.com/guardian/quizzical/blob/master/api/app/controllers/InternalApi.scala

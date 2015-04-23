# JSON Parsing

A comparison of the options available for consuming JSON in Scala.

## The challenge

As an example of the kind of thing we often want to do with JSON, let's try to do the following with each library.

1. Parse the following array of JSON objects from a String

    ```
    [{
      "id": 123,
      "title": "News article",
      "body": "The body",
      "mainImage": "image234",
      "tags": [ "tag345", "tag456", "tag789" ]
    },
    {
      "id": 999,
      "title": "Another news article",
      "body": "The other body",
      "tags": [ ]
    }]
    ```

2. Optionally deserialize it into case classes, if that makes things easier

3. Replace the `mainImage` field (a string) with an object, if that field exists

4. Replace each tag ID with an object if we have information about that tag, otherwise delete the ID from the array

5. Delete the `tags` field if it is an empty list

6. Stringify the resulting object

## json4s

### Notes

* Already used a lot in Guardian code.
* Uses reflection for (de)serialization -> slow

### Challenge results

Turned out surprisingly readable

## play-json

### Notes

* Uses macros for (de)serialization -> faster than json4s
* transformation API is highly functional, rather idiosynchratic

### Challenge results

Tried using the transformation API but gave up. It's elegant and functional, but I'm too stupid to understand it.

## jawn

### Notes

* Blazing fast parser + a minimal AST
* Designed to be plugged in to various frontends

### Challenge results

Haven't tried yet

## Argonaut

### Notes

* Very functional: lenses and zippers (cursors)

### Challenge results

Haven't tried yet

## rapture-json

### Notes

* Supports "natural" traversal of JSON using dot-notation, e.g. `json.fruits(1).color.as[String]`
* Nice feature: pattern matching on JSON

### Challenge results

Haven't tried yet

## Ruby

Just for comparison, I wrote the same code in Ruby.

```
main_image = { :id => "image234", :filename => "234.png" }
tags = {
  "tag345" => { :id => "tag345", :name => "news" },
  "tag789" => { :id => "tag789", :name => "sport" }
}

json = JSON.parse(input)

json.each { |x|
  if x["mainImage"]
    x["mainImage"] = main_image
  end

  x["tags"] = x["tags"].map { |t| tags[t] }.compact
  
  if x["tags"].empty?
    x.delete("tags")
  end
}

puts(json.to_json)
```

## Others

Didn't get around to looking at these.

* [sonofjson](https://github.com/wspringer/sonofjson) - Uses dynamic traversal style. Not much traction on GitHub
* [sjson](https://github.com/debasishg/sjson) - not maintained recently



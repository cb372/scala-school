require 'json'

input = <<-EOF
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
EOF

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

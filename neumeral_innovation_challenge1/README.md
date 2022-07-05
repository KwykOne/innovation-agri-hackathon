# Contextualized search

Challenge 1 of the ONDC Grand Hackathon

- Define a grammer to map product to purpose
- Define a product schema
- Build a Prototype

Team: Neumeral, contact: awin@neumeral.com
Repository available at: https://github.com/neumeral/contextual_search

## Overview

Uses the following components

- ElasticSearch as datastore
- Uses Flask API in the backend
- React frontend, to test the API

## Setup

Prerequisites: Elastic search 7.10.1 is running on port 9200
You can use docker to run Elastic search:
https://www.elastic.co/guide/en/elasticsearch/reference/7.10/docker.html

```
docker network create elastic
docker run --name es01 --net elastic -p 9200:9200 -p 9300:9300 -it docker.elastic.co/elasticsearch/elasticsearch:7.10.1
```

Uses Python 3.9

1. Setup virtual env

```
cd <project folder>
python3.9 -m venv venv #create virtual environment
source venv/bin/activate
pip install -r requirements/requirements.txt
```

2. Load data
   This loads data to Elastic search.

```
cd src/backend
python load_data.py
```

3. Run the Backed flask server
   runs flask on 5000 port

```
cd src/backend
python api.py
```

You can run curl at this point agaisnt the API.

4. Run the Frontend flask server
   Install npm packages
   runs react on 3000 port

```
cd src/frontend
npm install
npm run start
```

5. Give your inputs

## Solution

### Architecture

1. Validates the query syntax
   We use Python Lark to parse the query, and show parse errors

2. Translates from Hindi to English

The data we use is in English, so the queries has to be translated from Hindi to English to be compared against the values in the datastore. Alternatively, if the data is saved in the datastore is in Hindi then the translation step can be avoided. For translation we use [AWS Translate API](https://aws.amazon.com/translate/). This can be replaced by our own Neural Machine Translation models that supports Indic languages to English.

3. Compare against datastore

In the example we use ElasticSearch as the datastore. The query grammer is converted to a query in the datastore using adapters. Other adapters can be build (say, for eg. SQLAdapter that supports DB that can be queried using SQL).

### Query Grammer

The grammer is implemented in Python using [Lark - parsing toolkit](https://github.com/lark-parser/lark).

```
    start: query+
    query:  "terms:" terms ";"                                  -> search_terms
                | "type:"  p_type ";"                           -> product_type
                | "subtype:"  p_subtype ";"                     -> product_subtype
                | "contains:"  ingredients ";"                  -> product_ingredients
                | "subject:"  used_on_subject ";"               -> product_used_on
                | purpose                                       -> product_purpose
                | "during:"  time_when ";"                      -> product_used_during

    terms: SENTENCE

    p_type: /seed|sapling|nutrition|fungicide|pesticide|insecticide|fertilizer|implement|machinery|feed|agri-implements|supplements/
    p_subtype: /organic|chemical|natural|electric|mechanical/
    ingredients: SENTENCE

    used_on_subject: SENTENCE
    // used_on_subject: crop | animal | farm_type

    purpose: "action:"  action_string ";"  "on:"  objects ";"
    action_string: /protect from|preserve|increase|decrease|prevent|remove|treat/
    objects: SENTENCE
    // objects: disease | farm_output | pest

    time_when: SENTENCE
    // time_when: stage_cultivation | stage_growth

    WORD: LETTER+
    PUNCTUATION: WS | /,|: -"/
    SENTENCE: WORD | (WORD PUNCTUATION*)+

    %import common.LETTER
    %import common.WS
    %ignore WS
```

#### Examples:

1. Organic pesticide that contains neem extract to be used on potato, to prevent nematodes during plantation

```
type:pesticide;
subtype:organic;
contains:neem extract;
subject:potato;
action:prevent;
on:nematodes;
during:plantation;
```

2. Pesicides to protect from pests during plantation and harvest

```
type:pesticide;
action:protect from;
on:pests;
during:plantation, harvest;
```

### Product Schema

```python
class Product(TypedDict)
    name: str
    category: str
    subcategory: str
    description: str
    image_url: str
    ingredients: str
    benefits: str
    side_effects: str
    sustainability_score: float
    price: float
    used_on: str
    used_during: str
    purposes: str
    usages: str
    usage_codes: list[str]
    purpose_coes: list[str]
```

### API

#### Search in Hindi

```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"terms":"आम के लिए खाद","type":"उर्वरक", "action":"बढ़ोतरी", "on":"नमी", "subject":"आम का फल}' \
  http://localhost:5000/hi_IN/search
curl  "http://localhost:5000/hi_IN/search?q=terms:आम के लिए खाद;type:उर्वरक;action:बढ़ोतरी;on:नमी;subject:आम का फल;"
```

#### Search in English

```
curl  "http://localhost:5000/en/search?q=type:fertilizer;action:treat;on:Rhizobium;subject:Arecanut;"
```

_Note:_ The query supports English and Hindi

### UI

Input your conditions in the UI

## Scope for Improvement

- The biggest problem that we could face is to convert the data into the required product schema. NLP transformer models can be used to parse the description of the product given by the Provider, to answer a set of predefined queries that can return boolean or narrative answers.

For eg. "Is this product an organic product?" can be a pre-defined question to run against the model and the product description of the product to give a yes/no answer([ref: 1](#references)).

The same can be used to identify purposes, and usage details.

- Expanding and standardising a list of purpose an usage codes
  A list an be maintained, that can be updated, based on the kind of farm input products in the market.

## References

1. [Generate boolean (yes/no) questions from any content using T5 text-to-text transformer model](https://towardsdatascience.com/generating-boolean-yes-no-questions-from-any-content-using-t5-text-to-text-transformer-model-69f2744aff44)

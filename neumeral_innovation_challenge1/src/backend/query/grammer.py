# flake8: noqa

QUERY_GRAMMAR = """
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
    action_string: /protect from|growths|preserve|increase|decrease|prevent|remove|treat|reduce|improves|control/
    objects: SENTENCE
    // objects: disease | farm_output | pest

    time_when: SENTENCE
    // time_when: stage_cultivation | stage_growth

    NWS: (WS|" ")*
    WORD: LETTER+
    PUNCTUATION: WS | /,|: -"/
    SENTENCE: WORD | (WORD PUNCTUATION*)+

    %import common.LETTER
    %import common.WS
    %ignore WS

"""


example_1 = """
type:insecticide;
subtype:organic;
terms:organic insecticide;
contains:neem, papaya;
subject:cabbage, potato;
action:prevent;
on:nematodes;
during:harvest;
"""

example_2 = """
type:pesticide
action:protect from
on:pests
during:plantation, harvest
"""

examples = [example_1, example_2]

# Parsing
from query.evaluator import DefaultEvaluator
from query.parser import query_parser

parser = query_parser()

query_1 = """type:fertilizer;action:treat;on:Rhizobium;subject:Arecanut;"""

query_2 = """
terms:seed;
"""

evaluator = DefaultEvaluator(parser=parser)
# evaluator.build_query(query_1)
# query = evaluator.query_adapter.full_query()

results = evaluator.evaluate(query_1)
print("Search Results", results)

# ptree = parser.parse(query_1)

# print(query_1)
# print("-" * 80)
# print(ptree.pretty())

# print("=" * 80)
# print("=" * 80)


# print(query_2)
# ptree = parser.parse(query_2)
# print("-" * 80)
# print(ptree.pretty())

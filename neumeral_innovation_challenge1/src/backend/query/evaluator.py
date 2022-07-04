import os

from lark.exceptions import UnexpectedInput

DEFAULT_SEARCH_ADAPTER = "datastore.elastic_search_adapter.ElasticSearchAdapter"  # noqa


class QueryParseException(Exception):
    pass


def _adapter_name():
    adapter_paths = os.getenv("SEARCH_ADAPTER", DEFAULT_SEARCH_ADAPTER).split(".")
    class_name = adapter_paths.pop()
    module_name = ".".join(adapter_paths)
    return (module_name, class_name)


def get_adapter_instance():
    import importlib

    (module_name, class_name) = _adapter_name()
    module = importlib.import_module(module_name)
    klass = getattr(module, class_name)
    return klass()


class DefaultEvaluator:
    def __init__(self, parser, translator=None):
        self.parser = parser
        self.translator = translator
        self.query_adapter = get_adapter_instance()

    def build_datastore_query(self, instruction):

        if instruction.data == "search_terms":
            val = instruction.children[0].children[0].value
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_search_terms(val)
        elif instruction.data == "product_type":
            val = instruction.children[0].children[0].value
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_product_type(val)
        elif instruction.data == "product_subtype":
            val = instruction.children[0].children[0].value
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_product_subtype(val)
        elif instruction.data == "product_ingredients":
            val = instruction.children[0].children[0].value
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_product_ingredients(val)
        elif instruction.data == "product_used_on":
            val = instruction.children[0].children[0].value
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_product_used_on(val)
        elif instruction.data == "product_purpose":
            action = instruction.children[0].children[0].children[0].value
            acts_on = instruction.children[0].children[1].children[0].value
            val = " ".join([action, acts_on])
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_product_purpose(val)
        elif instruction.data == "product_used_during":
            val = instruction.children[0].children[0].value
            if self.translator:
                val = self.translator.translate(val)
            self.query_adapter.query_product_used_during(val)

    def build_query(self, query):
        try:
            parse_tree = self.parser.parse(query)

        except UnexpectedInput:
            raise QueryParseException("parse error")

        print("Parse Tree", parse_tree)
        for instruction in parse_tree.children:
            self.build_datastore_query(instruction)

    def evaluate(self, query):
        self.build_query(query)
        return self.query_adapter.search()

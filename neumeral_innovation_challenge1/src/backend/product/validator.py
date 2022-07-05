from jsonschema import validate
from jsonschema.exceptions import ValidationError


class SchemaValidator:
    def __init__(self, schema):
        self.schema = schema

    def validate(self, input_json):
        try:
            validate(instance=input_json, schema=self.schema)
        except ValidationError as err:
            print(err)
            err = "Invalid input"
            return False, err

import os

import boto3

AWS_ACCESS_KEY_ID = os.getenv("AWS_ACCESS_KEY_ID", "")
AWS_SECRET_ACCESS_KEY = os.getenv("AWS_SECRET_ACCESS_KEY", "")


translate = boto3.client(
    service_name="translate",
    region_name="us-east-1",
    use_ssl=True,
    aws_access_key_id=AWS_ACCESS_KEY_ID,
    aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
)

# Note:
# Our datastore has data in English,
# So to convert the indic languages query, we use a translator from AWS to convert to English


class HindiEngTranslator:
    def translate(self, input_str):
        result = translate.translate_text(Text=input_str, SourceLanguageCode="hi", TargetLanguageCode="en")
        return result.get("TranslatedText")


class TamilEngTranslator:
    def translate(self, input_str):
        result = translate.translate_text(Text=input_str, SourceLanguageCode="ta", TargetLanguageCode="en")
        return result.get("TranslatedText")

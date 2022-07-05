# For indic languages AWS translate is used
# Reference implementation using boto3
# https://docs.aws.amazon.com/translate/latest/dg/examples-python.html

import os

import boto3

AWS_ACCESS_KEY_ID = os.getenv("AWS_ACCESS_KEY_ID", "")
AWS_SECRET_ACCESS_KEY = os.getenv("AWS_SECRET_ACCESS_KEY", "")

# session = boto3.Session(
#     aws_access_key_id=AWS_ACCESS_KEY_ID,
#     aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
# )

# translate = session.resource(service_name="translate", region_name="us-east-1", use_ssl=True)

translate = boto3.client(
    service_name="translate",
    region_name="us-east-1",
    use_ssl=True,
    aws_access_key_id=AWS_ACCESS_KEY_ID,
    aws_secret_access_key=AWS_SECRET_ACCESS_KEY,
)

result = translate.translate_text(Text="बढ़ती", SourceLanguageCode="hi", TargetLanguageCode="en")


print("TranslatedText: " + result.get("TranslatedText"))
print("SourceLanguageCode: " + result.get("SourceLanguageCode"))
print("TargetLanguageCode: " + result.get("TargetLanguageCode"))

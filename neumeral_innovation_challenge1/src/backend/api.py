# Uses Flask API
from flask import Flask, jsonify, request
from flask_cors import CORS
from query.evaluator import DefaultEvaluator, QueryParseException
from query.lang_translators import HindiEngTranslator
from query.parser import query_parser

app = Flask(__name__)
CORS(app)


# API Endpoint for search, takes a search query text
# Returns JSON response of products
# Example use:
# /search?q=type: pesticide subtype: with active ingredients action: treat subject: potato cyst nematode when: harvest
@app.route("/hi_IN/search", methods=["GET"])
def search_hi():
    args = request.args

    query = args.get("q")
    print(query)

    parser = query_parser()
    translator = HindiEngTranslator()

    evaluator = DefaultEvaluator(parser=parser, translator=translator)

    try:
        count, results = evaluator.evaluate(query)
        return jsonify({"results": results, "count": count, "success": True})

    except QueryParseException as exp:
        return jsonify({"success": False, "message": str(exp)})


@app.route("/en/search", methods=["GET"])
def search_en():
    args = request.args

    query = args.get("q")
    print(query)

    parser = query_parser()

    evaluator = DefaultEvaluator(parser=parser)

    try:
        count, results = evaluator.evaluate(query)
        return jsonify({"results": results, "count": count, "success": True})

    except QueryParseException as exp:
        return jsonify({"success": False, "message": str(exp)})


if __name__ == "__main__":
    app.run(debug=True)

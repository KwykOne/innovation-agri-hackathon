## Environment Setup

pip install -U pip wheel setuptools
pip install pip-tools

## For translation using MBart

pip install torch torchvision torchaudio

pip install transformers
pip install sentencepiece

Refer mbart_translation_example.ipynb

## Spacy models download

python -m spacy download en_core_web_sm
python -m spacy download en_core_web_md

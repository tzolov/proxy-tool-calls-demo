


Prepare python venv environment:

```
python3 -m venv venv
source ./venv/bin/activate
(venv) pip install --upgrade pip
(venv) pip install -U openai python-dotenv
(venv) export OPENAI_API_KEY=YOUR KEY
```

Run python function calling using the OpenAI Python API (the ProxyApplication must be running):
```
(venv) python ./src/main/resources/functions.py
```
or for streaming:

```
(venv) python ./src/main/resources/functions-stream.py
```

Deactivate and clean the venv environment:

```
(venv) deactivate
rm -Rf ./venv
```


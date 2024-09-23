

```
python3 -m venv venv
source ./venv/bin/activate
(venv) pip install --upgrade pip
(venv) pip install -U openai python-dotenv
(venv) export OPENAI_API_KEY=YOUR KEY
(venv) python ./src/main/resources/functions.py
```

```
(venv) deactivate
rm -Rf ./venv
```


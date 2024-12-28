import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import requests
import json
import io

with io.open('./winning_percent.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

print(json.dumps(data, indent=4, ensure_ascii=False))
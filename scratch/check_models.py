import requests

api_key = "AIzaSyDuohBOa6KsLSTGl35B-NLby_44MJCtlb8"
url = f"https://generativelanguage.googleapis.com/v1beta/models?key={api_key}"

response = requests.get(url)
print(response.json())

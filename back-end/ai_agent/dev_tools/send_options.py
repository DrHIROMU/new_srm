import httpx

url = "http://127.0.0.1:8082/api/chat"
headers = {
    "Origin": "http://localhost:4200",
    "Access-Control-Request-Method": "POST",
    "Access-Control-Request-Headers": "content-type",
}

response = httpx.options(url, headers=headers)

print("Status:", response.status_code)
print("Headers:", dict(response.headers))
print("Body:", response.text)


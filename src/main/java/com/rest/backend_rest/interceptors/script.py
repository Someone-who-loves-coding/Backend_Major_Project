import requests
import time
from datetime import datetime, timedelta

token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwcmFraGFyMTJzYXhlbmFzQGdtYWlsLmNvbSIsImlhdCI6MTc0Njg2NjAzOSwiZXhwIjoxNzQ2OTc0MDM5fQ.4K_SGFEFy3-F1JyHckMlC66AiZCxY-DU15Z5wZAGwdQ"
url_template = "http://localhost:8080/api/v1/entry?date={}"
headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}

start_date = datetime.strptime("2025-04-01", "%Y-%m-%d")
end_date = datetime.strptime("2025-04-30", "%Y-%m-%d")
current_date = start_date

while current_date <= end_date:
    date_str = current_date.strftime("%Y-%m-%d")
    print(f"Posting for {date_str}...")

    try:
        response = requests.post(url_template.format(date_str), headers=headers, json={})
        print(f"Status: {response.status_code}, Response: {response.text}")

        if 200 <= response.status_code < 300:
            time.sleep(5)  # Wait only if the response is successful

    except requests.RequestException as e:
        print(f"Request failed for {date_str}: {e}")

    current_date += timedelta(days=1)

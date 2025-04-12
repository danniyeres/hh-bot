import requests

from config import BACKEND_URI, REDIRECT_URI, CLIENT_ID, AUTH_URI


def get_vacancies(user_id, text, area):
    url = f"{BACKEND_URI}/vacancy/get"

    response = requests.get(url, params={"user_id": user_id , "text": text, "area": area})
    if response.status_code == 200:
        return response.json()
    else:
        return None


def auth_user():
    url = f"{AUTH_URI}?response_type=code&client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}"
    return url


def send_response():
    url = f"{BACKEND_URI}/vacancy/response"
    return url
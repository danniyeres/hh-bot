import logging

import requests

logger = logging.getLogger(__name__)
from config import BACKEND_URI, REDIRECT_URI, CLIENT_ID, AUTH_URI



def auth_user_url():
    url = f"{AUTH_URI}?response_type=code&client_id={CLIENT_ID}&redirect_uri={REDIRECT_URI}"
    return url


def is_authorized(telegram_id: str) -> bool:
    try:
        response = requests.get(
            f"{BACKEND_URI}/oauth/telegram_user/{telegram_id}",
            timeout=5
        )
        logger.info("Checking authorization for user %s", telegram_id)
        if response.status_code == 200:
            logger.info("User %s is authorized", telegram_id)
            return True
        else:
            return False
    except requests.exceptions.RequestException as e:
        logger.error(f"Authorization check failed: {e}")
        return False


def send_response_url():
    url = f"{BACKEND_URI}/vacancy/response"
    return url


def get_area_id(city_name: str) -> int | None:

    url = "https://api.hh.ru/areas"
    response = requests.get(url)

    if response.status_code != 200:
        return None

    areas = response.json()

    def find_city(areas_list, target_city):
        for area in areas_list:
            if area["name"].lower() == target_city.lower():
                return area["id"]
            if "areas" in area:
                found = find_city(area["areas"], target_city)
                if found:
                    return found
        return None

    return find_city(areas, city_name)
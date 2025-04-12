from dotenv import load_dotenv
import os

load_dotenv()


BOT_TOKEN = os.getenv("BOT_TOKEN")
BACKEND_URI = os.getenv("BACKEND_URI")

REDIRECT_URI = os.getenv("REDIRECT_URI")
CLIENT_ID = os.getenv("CLIENT_ID")

AUTH_URI = os.getenv("AUTH_URI")
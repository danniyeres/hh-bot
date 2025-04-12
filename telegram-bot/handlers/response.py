import requests
from aiogram.dispatcher import router
from aiogram.filters import Command
from aiogram.types import Message

from utils import api

router = router.Router()

@router.message(Command("response"))
async def send_response(message:Message):

    url = api.send_response()
    user_id = str(message.from_user.id)
    response = requests.post(url, params={"telegram_id":user_id, "search_text": "Python", "area": 40})

    if response.status_code == 200:
        await message.answer("Отклик отправлен!")
    else:
        await message.answer("Не удалось отправить отклик. Попробуйте позже.")
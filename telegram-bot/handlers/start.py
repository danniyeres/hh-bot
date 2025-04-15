from aiogram.dispatcher import router
from aiogram.filters import Command
from aiogram.types import Message, InlineKeyboardMarkup, InlineKeyboardButton

from utils import api

router = router.Router()

@router.message(Command("start"))
async def start(message:Message):

    try:
        telegram_id = str(message.from_user.id)

        if api.is_authorized(telegram_id):
            await message.answer("Вы уже авторизованы.")
            return

        chat_id = message.chat.id
        state_data = f"{telegram_id}:{chat_id}"
        auth_url = f"{api.auth_user_url()}&state={state_data}"

        keyboard = InlineKeyboardMarkup(inline_keyboard=[
            [InlineKeyboardButton(text="🔗 Авторизоваться в HH", url=auth_url)]
        ])

        await message.answer("Привет! Авторизуйся в hh.ru, чтобы бот мог откликаться на вакансии.", reply_markup=keyboard)

        if api.is_authorized(telegram_id):
            await message.answer("Вы авторизованы. Теперь вы можете использовать бота.")
            return

    except Exception as e:
        await message.answer("Произошла ошибка. Пожалуйста, попробуйте позже.")
        print(f"Error in start command: {e}")
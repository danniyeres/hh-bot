from aiogram.types import ReplyKeyboardMarkup, KeyboardButton

cancel_button = ReplyKeyboardMarkup (
    keyboard=[
        [KeyboardButton(text="Отмена")]
    ],
    resize_keyboard=True,
    one_time_keyboard=True
)


cancel_skip_button = ReplyKeyboardMarkup (
    keyboard=[
        [KeyboardButton(text="Отмена"), KeyboardButton(text="Пропустить")]
    ],
    resize_keyboard=True,
    one_time_keyboard=True
)
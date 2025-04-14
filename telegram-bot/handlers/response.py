from aiogram import Router, types, F
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
from states.user_states import ResponseStates
import requests
from utils import api

router = Router()

@router.message(Command("response"))
async def start_response(message: types.Message, state: FSMContext):
    await message.answer("🔍 Введите search_text (например, 'Python'):")
    await state.set_state(ResponseStates.waiting_for_search_text)

@router.message(ResponseStates.waiting_for_search_text)
async def process_search_text(message: types.Message, state: FSMContext):
    if not message.text:
        await message.answer("❌ Текст не может быть пустым. Попробуйте снова:")
        return

    await state.update_data(search_text=message.text)
    await message.answer("📌 Теперь введите area (например, 40):")
    await state.set_state(ResponseStates.waiting_for_area)

@router.message(ResponseStates.waiting_for_area)
async def process_area(message: types.Message, state: FSMContext):
    try:
        area = int(message.text)
    except ValueError:
        await message.answer("❌ Area должна быть числом! Введите снова:")
        return

    data = await state.get_data()
    search_text = data.get("search_text")

    url = api.send_response()
    user_id = message.from_user.id
    response = requests.post(url,params={"telegram_id": user_id, "search_text": search_text, "area": area})
    await message.answer("🔄 Отправляем отклик...")

    if response.status_code == 200:
        await message.answer("✅ Отклик успешно отправлен!")
    else:
        await message.answer("❌ Ошибка при отправке. Попробуйте позже.")

    await state.clear()
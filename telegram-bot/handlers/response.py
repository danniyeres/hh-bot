import logging
from aiogram import Router, types, F
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
from aiogram.types import ReplyKeyboardRemove

from keyboards import reply
from states.user_states import ResponseStates
import requests
from utils import api

router = Router()
logger = logging.getLogger(__name__)


@router.message(Command("response"))
async def start_response(message: types.Message, state: FSMContext):
    try:
        await message.answer(
            "🔍 Введите search_text (например, 'Java Developer'):",
            reply_markup=reply.cancel_button
        )
        await state.set_state(ResponseStates.waiting_for_search_text)
    except Exception as e:
        logger.error(f"Error in start_response: {e}")
        await message.answer("⚠️ Произошла ошибка. Пожалуйста, попробуйте позже.")


@router.message(ResponseStates.waiting_for_search_text)
async def process_search_text(message: types.Message, state: FSMContext):
    try:
        if not message.text:
            await message.answer(
                "❌ Текст не может быть пустым. Попробуйте снова:",
                reply_markup=reply.cancel_button
            )
            return

        if message.text == "Отмена":
            await cancel_handler(message, state)
            return

        await state.update_data(search_text=message.text)
        await message.answer(
            "📌 Теперь введите город (например, 'Алматы'):",
            reply_markup=reply.cancel_button
        )
        await state.set_state(ResponseStates.waiting_for_city)
    except Exception as e:
        logger.error(f"Error in process_search_text: {e}")
        await message.answer("⚠️ Произошла ошибка. Пожалуйста, попробуйте позже.")


@router.message(ResponseStates.waiting_for_city)
async def process_city(message: types.Message, state: FSMContext):
    try:
        if message.text == "Отмена":
            await cancel_handler(message, state)
            return

        city_name = message.text.strip()
        area_id = api.get_area_id(city_name)

        if not area_id:
            await message.answer(
                f"❌ Город '{city_name}' не найден. Попробуйте снова:",
                reply_markup=reply.cancel_button
            )
            return

        await state.update_data(city=city_name, area_id=area_id)
        await message.answer(
            "Введите сопроводительного письма (или пропустите):",
            reply_markup=reply.cancel_skip_button
        )
        await state.set_state(ResponseStates.waiting_for_message)

    except Exception as e:
        logger.error(f"Error in process_city: {e}")
        await message.answer(
            "⚠️ Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.",
            reply_markup=ReplyKeyboardRemove()
        )



@router.message(ResponseStates.waiting_for_message)
async def process_message(message: types.Message, state: FSMContext):
    try:
        data = await state.get_data()
        search_text = data.get("search_text")
        city_name = data.get("city")
        area_id = data.get("area_id")

        if message.text in ["Отмена", "Пропустить"]:
            if message.text == "Отмена":
                await cancel_handler(message, state)
                return
            else:
                message_text = "null"
        else:
            message_text = message.text.strip()

        await message.answer("🔄 Отправляем отклик...")
        logger.info(
            f"Отклик отправлен пользователю {message.from_user.id} "
            f"на вакансии с текстом '{search_text}' и городом '{city_name}' (ID: {area_id})"
        )

        response = requests.post(
            api.send_response_url(),
            params={
                "telegram_id": message.from_user.id,
                "search_text": search_text,
                "area": area_id,
                "message": message_text
            },
            timeout=10
        )

        if response.status_code == 200:
            await message.answer(
                f"✅ Отклик отправлен! (Город: {city_name}, ID: {area_id})",
                reply_markup=ReplyKeyboardRemove()
            )
        else:
            await message.answer(
                f"❌ Ошибка при отправке (код {response.status_code}). Попробуйте позже.",
                reply_markup=ReplyKeyboardRemove()
            )

    except Exception as e:
        logger.error(f"Error in process_message: {e}")
        await message.answer(
            "⚠️ Произошла ошибка. Пожалуйста, попробуйте позже."
        )


@router.message(F.text == "Отмена")
async def cancel_handler(message: types.Message, state: FSMContext):
    try:
        await state.clear()
        await message.answer(
            "❌ Действие отменено.",
            reply_markup=ReplyKeyboardRemove()
        )
    except Exception as e:
        logger.error(f"Error in cancel_handler: {e}")
        await message.answer(
            "⚠️ Произошла ошибка при отмене. Пожалуйста, попробуйте ещё раз."
        )


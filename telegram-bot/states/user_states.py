from aiogram.fsm.state import StatesGroup, State

class ResponseStates(StatesGroup):
    waiting_for_search_text = State()
    waiting_for_city = State()
    waiting_for_message = State()
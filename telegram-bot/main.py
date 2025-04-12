import asyncio
import logging

from aiogram import Bot, Dispatcher
from aiogram.types import BotCommand

from handlers import start, response
from config import BOT_TOKEN

bot = Bot(token=BOT_TOKEN)
dp = Dispatcher()

logging.basicConfig(level=logging.INFO)

async def set_commands():
    commands = [
        BotCommand(command="/start", description="Registration"),
        BotCommand(command="/response", description="Send response"),
    ]
    await bot.set_my_commands(commands)


async def main():
    dp.include_router(start.router)
    dp.include_router(response.router)
    await set_commands()
    await dp.start_polling(bot)


if __name__ == '__main__':
    asyncio.run(main())

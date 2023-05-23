"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const cluster_1 = __importDefault(require("cluster"));
const bot_master_1 = require("./bot/bot.master");
const env_1 = require("./env");
const bot_worker_1 = require("./bot/bot.worker");
if (cluster_1.default.isPrimary) {
    // @ts-ignore
    const bot = globalThis['bot'] = new bot_master_1.BotMaster();
    bot.create(env_1.BOT_WORKER);
}
else {
    // @ts-ignore
    const worker = globalThis["worker"] = new bot_worker_1.BotWorker();
}

import cluster from "cluster";
import {BotMaster} from "./bot/bot.master";
import {BOT_WORKER} from "./env";
import {BotWorker} from "./bot/bot.worker";

if (cluster.isPrimary) {
    // @ts-ignore
    const bot = globalThis['bot'] = new BotMaster();
    bot.create(BOT_WORKER).start();
} else {
    // @ts-ignore
    const worker = globalThis["bworker"] = new BotWorker();
}
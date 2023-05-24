import cluster from "cluster";
import {BotMaster} from "./bot/bot.master";
import {BotWorker} from "./bot/bot.worker";
import {BOT_WORKER, MODE} from "./env";

if (cluster.isPrimary) {
    console.log("Running profile: %s", MODE);

    // @ts-ignore
    const bot = globalThis['bot'] = new BotMaster().create(BOT_WORKER);
    bot.start();
} else {
    // @ts-ignore
    const worker = globalThis["bworker"] = new BotWorker();
}
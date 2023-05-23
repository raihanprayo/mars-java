"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.BotMaster = void 0;
const telegraf_1 = require("telegraf");
const env_1 = require("../env");
const cluster_1 = __importDefault(require("cluster"));
const guards_1 = require("../utils/guards");
const events_1 = __importDefault(require("events"));
class BotMaster extends events_1.default {
    bot = new telegraf_1.Telegraf(env_1.BOT_TOKEN);
    items = [];
    index = -1;
    constructor() {
        super({ captureRejections: true });
    }
    create(total, env = {}, cb) {
        for (let i = 0; i < total; i++)
            this.createWorker(env, cb);
    }
    createWorker(env = {}, cb) {
        const currentIndex = this.items.length;
        const worker = cluster_1.default.fork(Object.assign({
            BOT_WORKER_INDEX: currentIndex
        }, process.env, env));
        this.items.push(worker);
        cb?.(currentIndex, worker);
        worker.on('message', (message, handle) => {
            this.emit(`worker-${currentIndex}:message`, message, handle);
        });
        return worker;
    }
    get(index = null) {
        if ((0, guards_1.isDefined)(index)) {
            return this.items[index];
        }
        else {
            this.index += 1;
            if (this.index === this.items.length)
                this.index = 0;
            return this.items[this.index];
        }
    }
}
exports.BotMaster = BotMaster;

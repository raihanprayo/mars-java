"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.BotWorker = void 0;
const events_1 = __importDefault(require("events"));
const cluster_1 = __importDefault(require("cluster"));
class BotWorker extends events_1.default {
    worker_index;
    constructor() {
        super({ captureRejections: true });
        if (!cluster_1.default.isWorker)
            throw new Error('Bot worker cannot be instantiate in primary process');
        this.worker_index = Number(process.env.BOT_WORKER_INDEX);
        process.on('message', this.onReceivedMessage.bind(this));
    }
    onReceivedMessage(message, sendHandle) {
    }
}
exports.BotWorker = BotWorker;

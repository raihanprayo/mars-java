import EventEmitter from "events";
import cluster from "cluster";
import {EventID, WorkerEvent} from "../utils/types";
import {formatWithOptions} from "util";
import type {Update} from "typegram";

export class BotWorker extends EventEmitter {
    private readonly worker_index: number;

    constructor() {
        super({captureRejections: true});
        if (!cluster.isWorker) throw new Error('Bot worker cannot be instantiate in primary process');

        this.worker_index = Number(process.env.BOT_WORKER_INDEX);
        process.on('message', this.onReceivedMessage.bind(this));
    }

    get id() {
        return this.worker_index;
    }

    private onReceivedMessage(message: WorkerEvent, sendHandle: unknown) {
        console.log('[WORKER-%s] Received update from master', this.id);
        switch (message.id) {
            case EventID.TELEGRAM_UPDATE:
                this.onTelegramUpdate(message.data);
                break;
        }
    }

    private onTelegramUpdate(update: Update) {
        if ('message' in update) {

        }
    }

}
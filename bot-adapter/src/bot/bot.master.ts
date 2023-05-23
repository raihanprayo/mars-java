import {Telegraf} from "telegraf";
import {BOT_TOKEN, BOT_WITEL, BOT_WORKER} from "../env";
import cluster, {Worker} from "cluster";
import {isDefined} from "../utils/guards";
import EventEmitter from "events";
import {EventID, Witel} from "../utils/types";
import {Serializable} from "worker_threads";

type WorkerCallback = (i: number, worker: Worker) => void;

export class BotMaster extends EventEmitter {

    private readonly bot: Telegraf;
    private readonly items: Worker[] = [];
    private index = -1;

    constructor() {
        super({captureRejections: true});
        this.bot = new Telegraf(BOT_TOKEN);
        process.once('SIGINT', () => this.bot.stop('SIGINT'));
        process.once('SIGTERM', () => this.bot.stop('SIGTERM'));
        console.log('Initialize Bot Master (%s)', Witel[BOT_WITEL]);
    }

    create(total: number, env: NodeJS.Dict<string> = {}, cb?: WorkerCallback) {
        for (let i = 0; i < total; i++)
            this.createWorker(env, cb);

        return this;
    }

    createWorker(env: NodeJS.Dict<string> = {}, cb?: WorkerCallback) {
        const currentIndex = this.items.length;
        const worker = cluster.fork(Object.assign({
            BOT_WORKER_INDEX: currentIndex
        }, process.env, env));
        console.log('Create New worker-%s (pid %s)', currentIndex, worker.process.pid);


        this.items.push(worker);
        cb?.(currentIndex, worker);

        worker.on('message', (message, handle) => {
            this.emit(`worker-${currentIndex}:message`, message, handle);
        });

        return worker;
    }

    get(): Worker
    get(index: number): Worker
    get(index: number | null | undefined = null) {
        if (isDefined(index)) {
            return this.items[index];
        } else {
            this.index += 1;
            if (this.index === this.items.length)
                this.index = 0;
            return this.items[this.index];
        }
    }

    send(message: Serializable) {
        this.get().send(message);
    }

    start() {
        this.bot.on('message', (a) => {
            this.send({
                id: EventID.TELEGRAM_UPDATE,
                data: a.update
            });
        });

        return this.bot.launch();
    }

}
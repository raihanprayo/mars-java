import {Telegraf} from "telegraf";
import {BOT_DATEL, BOT_NAME, BOT_TOKEN, BOT_WITEL, BOT_WORKER} from "../env";
import cluster, {Worker} from "cluster";
import {isDefined} from "../utils/guards";
import EventEmitter from "events";
import {EventID, Witel} from "../utils/types";
import {Serializable} from "worker_threads";

type WorkerCallback = (pid: number, worker: Worker) => void;

export class BotMaster extends EventEmitter {

    private readonly bot: Telegraf;
    private readonly items = new Map<number, Worker>();
    private index = -1;

    constructor() {
        super({captureRejections: true});
        this.bot = new Telegraf(BOT_TOKEN);
        process.once('SIGINT', () => this.bot.stop('SIGINT'));
        process.once('SIGTERM', () => this.bot.stop('SIGTERM'));
        console.log('Initialize Bot Master (%s - %s)', Witel[BOT_WITEL], BOT_NAME);
    }

    create(total: number, env: NodeJS.Dict<string> = {}, cb?: WorkerCallback) {
        for (let i = 0; i < total; i++)
            this.createWorker(env, cb);

        return this;
    }

    createWorker(env: NodeJS.Dict<string> = {}, cb?: WorkerCallback) {
        const worker = cluster.fork(Object.assign({}, process.env, env));
        console.log('Created new bot-worker (pid %s)', worker.process.pid);


        this.items.set(worker.process.pid!, worker);
        cb?.(worker.process.pid!, worker);

        worker.on('message', (message, handle) => {
            this.emit(`worker-${worker.process.pid}:message`, message, handle);
        });

        return worker;
    }

    get(): Worker
    get(pid: number): Worker
    get(pid: number | null | undefined = null) {
        if (isDefined(pid)) {
            if (!this.items.has(pid)) throw new Error("No worker with pid " + pid);
            return this.items.get(pid)
        } else {
            this.index += 1;
            if (this.index === this.items.size)
                this.index = 0;
            const workerArr = [...this.items.values()]
            return workerArr[this.index];
        }
    }

    send(message: Serializable): void
    send(pid: number, message: Serializable): void
    send(pidOrMessage: any, message?: any): void {
        if (arguments.length === 2)
            this.get(pidOrMessage).send(message);
        else
            this.get().send(pidOrMessage);
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
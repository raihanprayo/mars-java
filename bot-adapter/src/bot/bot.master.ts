import { Context, NarrowedContext, Telegraf } from "telegraf";
import { BOT_NAME, BOT_TOKEN, BOT_WITEL } from "../env";
import cluster, { Worker } from "cluster";
import { isDefined } from "../utils/guards";
import EventEmitter from "events";
import { Witel } from "../utils/types";
import { CallbackQuery, Message, Update } from "typegram";
import { randomString } from "../utils/string";
import { EventType, MasterEvent, Payload, me } from "../events";

type WorkerCallback = (id: string, worker: Worker) => void;

export class BotMaster extends EventEmitter {
	private readonly bot: Telegraf;
	private readonly items = new Map<string, Worker>();
	private index = -1;

	constructor() {
		if ("bot" in globalThis) {
			throw new Error("Bot master already running in current thread");
		}

		super({ captureRejections: true });
		this.bot = new Telegraf(BOT_TOKEN);
		process.once("SIGINT", () => this.bot.stop("SIGINT"));
		process.once("SIGTERM", () => this.bot.stop("SIGTERM"));
		log.info("Initialize Bot Master (%s - %s)", Witel[BOT_WITEL], BOT_NAME);

		Object.defineProperty(globalThis, "bot", {
			value: this,
		});
	}

	create(total: number, env: NodeJS.Dict<string> = {}, cb?: WorkerCallback) {
		for (let i = 0; i < total; i++) this.createWorker(env, cb);

		return this;
	}

	createWorker(env: NodeJS.Dict<string> = {}, cb?: WorkerCallback) {
		const id = randomString(8, false);

		const worker = cluster.fork(
			Object.assign({}, process.env, env, {
				BOT_WORKER_ID: id,
			})
		);
		worker["name"] = id;

		log.info("Created new bot-worker (id %s)", id);

		this.items.set(id, worker);
		cb?.(id, worker);

		worker.on("message", (message, handle) => {
			this.emit(`worker-${worker.process.pid}:message`, message, handle);
		});
		worker.send({
			id: EventType.SETTINGS,
			data: settings.raw,
		});
		return worker;
	}

	get(): Worker;
	get(id: string): Worker;
	get(id: string | null | undefined = null) {
		if (isDefined(id)) {
			if (!this.items.has(id)) {
				throw new Error("No worker with id " + id);
			}
			return this.items.get(id);
		} else {
			// Round robin iteration
			this.index += 1;
			if (this.index === this.items.size) {
				this.index = 0;
			}
			const workerArr = [...this.items.values()];
			return workerArr[this.index];
		}
	}

	send<D = any>(message: MasterEvent<D>): void;
	send<D = any>(id: string, message: MasterEvent<D>): void;
	send(idOrMessage: any, message?: any): void {
		if (arguments.length === 2) {
			this.get(idOrMessage).send(message);
		} else {
			this.get().send(idOrMessage);
		}
	}

	start() {
		this.bot.on("photo", (ctx, b) => {
			const message = ctx.message;

			const text = message.caption;
			const entities = message.caption_entities;
		});
		this.bot.on("callback_query", (ctx, b) => {
			const query = ctx.callbackQuery;

			if ("data" in query) {
				const { id, data, from, message } = query;
				this.send<Payload.TelegramCallbackQuery>(
					me(EventType.TG_CALLBACK_QUERY, {
						id: message!.message_id,
						chat: message!.chat,
						from,
						reply_id: id,
						reply_data: data,
					})
				);
			}
		});

		return this.bot.launch();
	}

	private onPhoto(
		ctx: NarrowedContext<
			Context<Update>,
			{
				message: Update.New & Update.NonChannel & Message.PhotoMessage;
				update_id: number;
			}
		>
	) {}
}

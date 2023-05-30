import { Chat, MessageEntity, PhotoSize, User } from "typegram";
import type { BotWorker } from "./bot/bot.worker";
import { randomString } from "./utils/string";

export function me<D>(type: EventType, data: D): MasterEvent<D> {
	return {
		id: randomString(5),
		type,
		data,
		timestamp: new Date().getMilliseconds(),
	};
}
export function we<D>(type: EventType, data: D): WorkerEvent<D> {
	const worker: BotWorker = globalThis.worker;
	return {
		id: randomString(5),
		type,
		data,
		worker: worker.id,
		timestamp: new Date().getMilliseconds(),
	};
}

export enum EventType {
	SETTINGS,
	TG_CALLBACK_QUERY,
	TG_MESSAGE,
	TG_CAPTION,
}

interface BaseEvent<D = any> {
	readonly id: string;
	type: EventType;
	data: D;
	timestamp: number;
}
export interface MasterEvent<D = any> extends BaseEvent<D> {}

export interface WorkerEvent<D = any> extends BaseEvent<D> {
	worker: string;
}

export namespace Payload {
	export interface TelegramCallbackQuery {
		id: number;
		chat: Chat;
		from: User;
		reply_id: string;
		reply_data: string;
	}
	export interface TelegramMessage {
		id: number;
		chat: Chat;
		from: User;

		isCommand: boolean;
		text: string;
		text_entities?: MessageEntity[];
	}
	export interface TelegramCaption {
		id: number;
		chat: Chat;
		from: User;

		photo: PhotoSize[];
		isCommand: boolean;
		caption?: string;
		caption_entities?: MessageEntity[];
	}
}

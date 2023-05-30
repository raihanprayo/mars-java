import { Chat, Document, MessageEntity, PhotoSize, User } from "typegram";

export enum Witel {
	ROC = "ROC",
	BANTEN = "BANTEN",
	BEKASI = "BEKASI",
	BOGOR = "BOGOR",
	JAKBAR = "JAKBAR",
	JAKPUS = "JAKPUS",
	JAKSEL = "JAKSEL",
	JAKTIM = "JAKTIM",
	JAKUT = "JAKUT",
	TANGERANG = "TANGERANG",
}

export enum Product {
	INTERNET,
	IPTV,
	VOICE,
}

export enum TgEventID {
	UNKNOWN,
	MESSAGE,
	COMMAND,
	CAPTION_MESSAGE,
	CAPTION_COMMAND,
}

export namespace AppEvent {
	export interface Settings {
		key: string;
		value: any;
	}
}

export namespace Tg {
	export enum Source {
		GROUP,
		SUPER_GROUP,
		CHANNEL,
		PRIVATE,
	}

	export interface TgEvent {
		id: TgEventID;
	}

	export type TgChat = Chat & { type: Source };

	export interface MessageEvent extends TgEvent {
		message_id: number;

		chat: TgChat;
		date: number;
		from: User;

		text?: string;
		command?: MessageEntity & { cmd: string };
		photo?: PhotoSize[];
		document?: Document;
	}
}

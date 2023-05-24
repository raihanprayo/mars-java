import {Chat, Document, MessageEntity, PhotoSize, User} from "typegram";

export enum Witel {
    ROC,
    BANTEN,
    BEKASI,
    BOGOR,
    JAKBAR,
    JAKPUS,
    JAKSEL,
    JAKTIM,
    JAKUT,
    TANGERANG
};


export enum Product {
    INTERNET,
    IPTV,
    VOICE
}

export interface MasterEvent<D = any> {
    id: EventID;
    data: D;
}

export interface WorkerEvent<D = any> extends MasterEvent<D> {
    worker: number;
}

export enum EventID {
    TELEGRAM_UPDATE
}

export enum TgEventID {
    UNKNOWN,
    MESSAGE,
    COMMAND,
    CAPTION_MESSAGE,
    CAPTION_COMMAND
}

export namespace Tg {
    export enum Source {
        GROUP,
        SUPER_GROUP,
        CHANNEL,
        PRIVATE
    }

    export interface TgEvent {
        id: TgEventID;
    }

    export type TgChat = Chat & { type: Source }

    export interface MessageEvent extends TgEvent {
        message_id: number;

        chat: TgChat;
        date: number;
        from: User;

        text?: string;
        command?: MessageEntity & { cmd: string; }
        photo?: PhotoSize[];
        document?: Document;
    }
}
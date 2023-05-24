import EventEmitter from "events";
import cluster from "cluster";
import {EventID, MasterEvent, Tg, TgEventID} from "../utils/types";
import type {Update} from "typegram";
import {Chat, MessageEntity} from "typegram";
import Source = Tg.Source;

export class BotWorker extends EventEmitter {

    constructor() {
        if ('worker' in globalThis)
            throw new Error("Bot worker already running in current thread");

        super({captureRejections: true});
        if (!cluster.isWorker) {
            throw new Error('Bot worker cannot be instantiate in primary process');
        }

        process.on('message', this.onReceivedMessage.bind(this));
        Object.defineProperty(globalThis, 'worker', {
            value: this
        })
    }

    get id() {
        return process.pid;
    }

    private onReceivedMessage(message: MasterEvent, sendHandle: unknown) {
        log.info('[WORKER-%s] Received event from master', this.id);
        switch (message.id) {
            case EventID.TELEGRAM_UPDATE:
                this.onTelegramUpdate(message.data);
                break;
        }
    }

    private onTelegramUpdate(update: Update) {
        if ('message' in update) {
            const message = update.message;
            const event: Tg.MessageEvent = {
                id: TgEventID.UNKNOWN,
                message_id: message.message_id,
                chat: determineChatSource(message.chat),
                date: message.date,
                from: message.from!
            }

            if ('photo' in message) {
                event.photo = message.photo;
            }
            if ('document' in message) {
                event.document = message.document;
            }

            if ('text' in message) {
                let command: undefined | MessageEntity;
                const cmdIndex = (message.entities || []).findIndex(value => value.type === 'bot_command');
                const isCommand = cmdIndex !== -1;

                event.id = TgEventID.MESSAGE;
                event.text = message.text;
                if (isCommand) {
                    command = message.entities![cmdIndex];
                    event.id = TgEventID.COMMAND
                    event.command = {
                        ...command,
                        cmd: message.text.slice(command.offset, command.length)
                    }
                }
            }
            else if ('caption' in message) {
                let command: undefined | MessageEntity;
                const cmdIndex = (message.caption_entities || []).findIndex(value => value.type === 'bot_command');
                const isCommand = cmdIndex !== -1;

                event.id = TgEventID.CAPTION_MESSAGE;
                event.text = message.caption;
                if (isCommand) {
                    command = message.caption_entities![cmdIndex];
                    event.id = TgEventID.CAPTION_COMMAND;
                    event.command = {
                        ...command,
                        cmd: message.caption.slice(command.offset, command.length)
                    }
                }
            }

            this.emit(`tg:message`, Object.freeze(event));
        }
    }

}

function determineChatSource(chat: Chat): Tg.TgChat {
    let source: Tg.Source;
    switch (chat.type) {
        case "group":
            source = Source.GROUP
            break;
        case "private":
            source = Source.PRIVATE
            break;
        case "supergroup":
            source = Source.SUPER_GROUP
            break;
        case "channel":
            source = Source.CHANNEL
            break;
    }

    return Object.assign({}, chat, {
        type: source
    });
}
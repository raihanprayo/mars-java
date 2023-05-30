import cluster from "cluster";
import {BotWorker} from "./bot/bot.worker";
import {type Logger, LoggerFactory} from "./utils/logger";
import {createOption, program} from "commander";
import {Properties} from "./utils/properties";
import {BotMaster} from "./bot/bot.master";
import {BOT_TOKEN, BOT_WITEL, BOT_WORKER, MODE} from "./env";
import {Witel} from "./utils/types";
import {isDefined} from "./utils/guards";

globalThis.settings = new Properties({
    bot: {
        token: BOT_TOKEN,
        worker: BOT_WORKER,
        witel: BOT_WITEL
    }
});
settings.setParser("bot", {
    token: String,
    worker: Number,
    witel: v => {
        const value = Witel[v];
        if (isDefined((value))) return value;
        return BOT_WITEL;
    }
})


globalThis.factory = new LoggerFactory();
globalThis.log = factory.create();

if (cluster.isPrimary) {
    program
        .addOption(createOption("-D [...value]")
            .hideHelp(true)
            .default(new Properties())
            .argParser((value, previous: Properties) => {
                const equalIndex = value.indexOf("=");
                previous.set(
                    value.slice(0, equalIndex),
                    value.slice(equalIndex + 1)
                );
                return previous;
            }))
        .action((options) => {
            log.info("Running profile: %s", MODE);
            settings.imports(options.D);
            new BotMaster()
                .create(BOT_WORKER)
                .start();

        })
        .parse();
}
else {
    new BotWorker().on("tg:message", log.info);
}

declare global {
    /** global settings */
    var settings: Properties;
    var log: Logger;
    var factory: LoggerFactory;
}
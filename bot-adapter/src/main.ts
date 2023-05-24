import cluster from "cluster";
import {BotWorker} from "./bot/bot.worker";
import {type Logger, LoggerFactory} from "./utils/logger";
import {createOption, program} from "commander";
import {Properties} from "./utils/properties";
import {BotMaster} from "./bot/bot.master";
import {BOT_WORKER, MODE} from "./env";


globalThis.settings = new Properties();
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

            console.log(settings.raw);
        })
        .parse();
}
else {
    new BotWorker();
}

declare global {
    /** global settings */
    var settings: Properties;
    var log: Logger;
    var factory: LoggerFactory;
}
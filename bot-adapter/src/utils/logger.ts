import {isConvertableToBoolean, isDefined, isStr, isUndef, toBoolean} from "./guards";
import {PassThrough} from "stream";
import * as DateFns from "date-fns"
import {Colorize} from "./logger.color";
import {formatWithOptions, InspectOptions} from "util";
import {copyFileSync, createWriteStream, existsSync, mkdirSync, readdirSync, rmSync, statSync, WriteStream} from "fs";
import {join, parse} from 'path'
import cluster from "cluster";
import {Properties} from "./properties";
import {randomUUID} from "crypto";

export interface LoggerConfig {
    level: LogLevel;
    "date.format": string;
    "file.rolling-policy": LogRollingPolicy;
    "file.output"?: string | undefined;

    "inspect.showHidden"?: boolean | undefined;
    "inspect.depth"?: number | null | undefined;
    "inspect.colors"?: boolean | undefined;
    "inspect.customInspect"?: boolean | undefined;
    "inspect.showProxy"?: boolean | undefined;
    "inspect.maxArrayLength"?: number | null | undefined;
    "inspect.maxStringLength"?: number | null | undefined;
    "inspect.breakLength"?: number | undefined;
    "inspect.compact"?: boolean | number | undefined;
    "inspect.sorted"?: boolean | ((a: string, b: string) => number) | undefined;
    "inspect.getters"?: 'get' | 'set' | boolean | undefined;
    "inspect.numericSeparator"?: boolean | undefined;
}

export interface Logger {

    error(message: string, ...params: any[]): void;

    error(...args: any[]): void;

    warn(message: string, ...params: any[]): void;

    warn(...args: any[]): void;

    info(message: string, ...params: any[]): void;

    info(...args: any[]): void;

    debug(message: string, ...params: any[]): void;

    debug(...args: any[]): void;

    verbose(message: string, ...params: any[]): void;

    verbose(...args: any[]): void;
}

export enum LogLevel {
    ERROR,
    WARN,
    INFO,
    DEBUG,
    VERBOSE
}

export enum LogRollingPolicy {
    DAY = 1,
    SIZE = 2,
    DAY_N_SIZE = DAY | SIZE
}

export namespace LogLevel {
    export function alias(l: LogLevel) {
        switch (l) {
            case LogLevel.ERROR:
                return "ERR "
            case LogLevel.WARN:
                return "WARN"
            case LogLevel.INFO:
                return "INFO"
            case LogLevel.DEBUG:
                return "DEBG"
            case LogLevel.VERBOSE:
                return "VERB"
        }
    }
}

export class LoggerFactory {

    readonly stdout = new PassThrough({defaultEncoding: 'utf8'});
    readonly stderr = new PassThrough({defaultEncoding: 'utf8'});
    private cleaner: FileLogStream | undefined;

    constructor() {
        settings.set(`logging`, {
            level: LogLevel.INFO,
            inspect: {
                colors: true,
                depth: 3
            },
            date: {
                format: "dd/MM/yyyy HH:mm:ss"
            },
            file: {
                output: undefined,
                "rolling-policy": LogRollingPolicy.DAY
            }
        });
        settings.setParser("logging", {
            level: v => {
                const key = LogLevel[v];
                if (isDefined(key)) return Number(v);
                return LogLevel.INFO;
            },
            inspect: {
                colors: Boolean,
                depth: Number,
                showHidden: Boolean,
                customInspect: Boolean,
                showProxy: Boolean,
                maxArrayLength: Number,
                maxStringLength: Number,
                breakLength: Number,
                compact: Boolean,
                sorted: Boolean,
                getters: v => {
                    if (isConvertableToBoolean(v)) return toBoolean(v);
                    return ["get", "set"].includes(v.toLowerCase());
                },
                numericSeparator: Boolean
            },
            date: {
                format: String
            },
            file: {
                output: String,
                "rolling-policy": Number
            }
        })

        this.stdout.pipe(process.stdout);
        this.stderr.pipe(process.stderr);
        process.on('beforeExit', code => {
            this.stderr.destroy();
            this.stdout.destroy();
            this.cleaner?.destroy();
        });
    }

    set<K extends keyof LoggerConfig>(key: K, value: LoggerConfig[K]) {
        global.settings.set(`logging.${key}`, value);
        switch (key) {
            case "file.output": {
                const output = value as string | undefined;
                if (isDefined(output)) {
                    if (existsSync(output)) {
                        const stat = statSync(output)
                        if (stat.isDirectory()) {
                            throw new Error("'file.output' should target a file");
                        }
                    }
                    if (isDefined(this.cleaner)) {
                        this.cleaner.reset();
                    }
                    else {
                        this.cleaner = new FileLogStream(this);
                        this.stdout.pipe(this.cleaner);
                        this.stderr.pipe(this.cleaner);
                    }
                }
                else {
                    if (isUndef(this.cleaner)) {
                        return;
                    }
                    this.stdout.unpipe(this.cleaner);
                    this.stderr.unpipe(this.cleaner);
                    this.cleaner.destroy();
                    this.cleaner = undefined;
                }
                break;
            }
        }
    }

    get<K extends keyof LoggerConfig>(key: K): LoggerConfig[K]
    get<K extends keyof LoggerConfig>(key: K, defaults: LoggerConfig[K]): LoggerConfig[K]
    get<K extends keyof LoggerConfig>(key: K, defaults?: LoggerConfig[K]): LoggerConfig[K] {
        const value = global.settings.get(`logging.${key}`)
        if (arguments.length === 2) return isDefined(value) ? value : defaults!;
        return value;
    }

    create(): Logger {
        return new BotLogger(this);
    }

    flatten(message: any, params: any[]) {
        return formatWithOptions(global.settings.get("logging.inspect"), message, ...params);
    }
}

class FileLogStream extends PassThrough {
    private readonly filePath: string;

    private fis: WriteStream;
    private timestamp: Date = new Date();

    constructor(private readonly factory: LoggerFactory) {
        super({
            transform(chunk: Buffer, encoding, done) {
                if (Buffer.isBuffer(chunk)) {
                    const normalize = chunk.toString('utf8').replace(ANSII_CHAR_REGX, '');
                    chunk = Buffer.from(normalize);
                }
                done(null, chunk);
            }
        });

        this.filePath = factory.get("file.output")!;
        this.fis = createWriteStream(this.filePath, {
            encoding: 'utf8',
            autoClose: true,
            flags: 'a'
        });

        this.pipe(this.fis);
    }

    reset(removeFile = false) {
        this.unpipe(this.fis);
        this.fis.destroy();
        if (removeFile) rmSync(this.filePath);
        this.fis = createWriteStream(this.filePath, {
            encoding: 'utf8',
            autoClose: true,
            flags: 'a'
        });
        this.pipe(this.fis);
    }

    rolling(policy: LogRollingPolicy) {
        const currentTimestamp = new Date();
        switch (policy) {
            case LogRollingPolicy.DAY: {
                this.rollingByDay(currentTimestamp);
                break;
            }
            default:
                break;
        }
    }

    rollingByDay(currentTimestamp: Date) {
        if (DateFns.isSameDay(this.timestamp, currentTimestamp)) {
            this.timestamp = currentTimestamp;
            return;
        }

        const path = this.factory.get('file.output')!;

        const {dir, name} = parse(path);
        const archiveDir = join(dir, 'archive');
        if (!existsSync(dir)) {
            mkdirSync(archiveDir, {recursive: true});
        }

        copyFileSync(path, join(archiveDir, this.rollingFilename(name, archiveDir)));
        this.timestamp = currentTimestamp;
        this.reset(true);
    }

    rollingFilename(name: string, archiveDir: string) {
        const rollingPolicy = this.factory.get('file.rolling-policy');
        switch (rollingPolicy) {
            case LogRollingPolicy.DAY: {
                return `${name}.${DateFns.format(this.timestamp, "dd-MM-yyyy")}.log`;
            }
            case LogRollingPolicy.SIZE: {
                const total = readdirSync(archiveDir, 'utf8')
                    .filter(e => e.startsWith(name))
                    .length;
                return `${name}.${total}.log`
            }
            case LogRollingPolicy.DAY_N_SIZE: {
                const date = DateFns.format(this.timestamp, "dd-MM-yyyy");
                const total = readdirSync(archiveDir, 'utf8')
                    .filter(e => e.startsWith(name + "." + date))
                    .length;
                return `${name}.${date}.${total}.log`;
            }
        }
    }

    override destroy(error?: Error): this {
        this.unpipe(this.fis);
        this.fis.destroy(error);
        return super.destroy(error);
    }


    override write(chunk: any, encoding?: BufferEncoding, cb?: (error: Error | null | undefined) => void): boolean;
    override write(chunk: any, cb?: (error: Error | null | undefined) => void): boolean;
    override write(chunk: any, encoding?: any, cb?: any): boolean {
        this.rolling(this.factory.get('file.rolling-policy'));
        return super.write(chunk, encoding, cb);
    }
}

class BotLogger implements Logger {

    constructor(private readonly factory: LoggerFactory) {
    }

    private print(level: LogLevel, message: any, params: any[]) {
        if (!this.isLevelAllowed(level)) {
            return;
        }

        const date = new Date();
        const dateFormatted = DateFns.format(date, this.factory.get("date.format", "dd/MM/yyyy HH:mm:ss.zzz"));

        const logLevelElement = LogLevel[level] as keyof typeof Colorize.byLogLevel;
        const logLevelColorFn = Colorize.byLogLevel[logLevelElement];
        const flattedMessage = this.factory.flatten(message, params);

        const logFormat = [
            `[${logLevelColorFn(LogLevel.alias(level))}]`,
            cluster.isPrimary ? null : `Worker-${process.pid}`,
            logLevelColorFn(dateFormatted),
            "-",
            flattedMessage
        ].filter(isStr)

        const stream = this.isWarnOrError(level) ? this.factory.stderr : this.factory.stdout;
        stream.push(logFormat.join(" ") + "\n", "utf8");
    }

    private isLevelAllowed(level: LogLevel) {
        return level <= this.factory.get('level');
    }

    private isWarnOrError(level: LogLevel) {
        return level <= LogLevel.WARN;
    }

    debug(message: string, ...params: any[]): void;
    debug(...args: any[]): void;
    debug(msg: any, ...args: any[]): void {
        this.print(LogLevel.DEBUG, msg, args);
    }

    error(message: string, ...params: any[]): void;
    error(...args: any[]): void;
    error(msg: any, ...args: any[]): void {
        this.print(LogLevel.ERROR, msg, args);
    }

    info(message: string, ...params: any[]): void;
    info(...args: any[]): void;
    info(msg: any, ...args: any[]): void {
        this.print(LogLevel.INFO, msg, args);
    }

    verbose(message: string, ...params: any[]): void;
    verbose(...args: any[]): void;
    verbose(msg: any, ...args: any[]): void {
        this.print(LogLevel.VERBOSE, msg, args);
    }

    warn(message: string, ...params: any[]): void;
    warn(...args: any[]): void;
    warn(msg: any, ...args: any[]): void {
        this.print(LogLevel.WARN, msg, args);
    }

}

const ANSII_CHAR_REGX = /\x1b\[[0-9;]+m/g;
import {Witel} from "./utils/types";
import {cpus} from "os";
import {isDefined, isStr} from "./utils/guards";

type Mode = 'dev' | 'development' | 'prod' | 'production'
type EnvParseCallback<T = any> = (value: string | undefined, envKey: string) => T;

function env(key: string | string[]): string | undefined;
function env<T>(key: string | string[], cb: EnvParseCallback<T>): T;
function env(keys: string | string[], cb?: EnvParseCallback): any {
    keys = isStr(keys) ? [keys] : keys;

    let value;
    if (keys.length > 1) {
        for (let key of keys) {
            const envName = key.toUpperCase();
            value = process.env[envName];

            if (isDefined(value))
                return cb ? cb(value, envName) : value;
        }

        if (!isDefined(value))
            value = cb ? cb(value, keys[0]!.toUpperCase()) : value;
    } else {
        const envName = keys[0]!.toUpperCase();
        value = process.env[envName];
        value = cb ? cb(value, envName) : value;
    }
    return value;
}

export const MODE: Mode = env(['MODE', 'NODE_ENV'], (v) => {
    if (!v) return 'dev';
    const predicate: Mode[] = ['dev', 'development', 'prod', 'production'];

    if (!predicate.includes(v.toLowerCase() as any)) throw new TypeError('Invalid application mode');
    return v.toLowerCase() as Mode;
});

export const BOT_TOKEN = env('bot_token', (v, k) => {
    if (!v) throw new TypeError(`${k} env is undefined`);
    return v;
});

export const BOT_WORKER = env('bot_worker', (v, k) => {
    const total = v ? Number(v) : 2;
    if (total > cpus().length) throw Error('Bot worker total cannot be more than CPUs core');
    return total;
});
export const BOT_WITEL = env<Witel>('bot_witel', (v, k) => {
    if (!v) throw new TypeError(`${k} unconfigured WITEL location`);
    return Witel[v.toUpperCase() as any] as any;
});

export const BOT_DATEL = env('bot_datel');

export const BOT_NAME = env('bot_name', v => {
    if (v) return v;
    return `BOT-${Witel[BOT_WITEL]}-${BOT_DATEL||'A/N'}`;
});

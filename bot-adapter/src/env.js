"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.BOT_DATEL = exports.BOT_WITEL = exports.BOT_WORKER = exports.BOT_TOKEN = void 0;
const types_1 = require("./utils/types");
const os_1 = require("os");
function env(key, cb) {
    const envName = key.toUpperCase();
    if (cb)
        return cb(process.env[envName], envName);
    return process.env[envName];
}
exports.BOT_TOKEN = env('bot_token', (v, k) => {
    if (!v)
        throw new TypeError(`${k} env is undefined`);
    return v;
});
exports.BOT_WORKER = env('bot_worker', (v, k) => {
    const total = v ? Number(v) : 2;
    if (total > (0, os_1.cpus)().length)
        throw Error('Bot worker total cannot be more than CPUs core');
    return total;
});
exports.BOT_WITEL = env('bot_witel', (v, k) => {
    if (!v)
        throw new TypeError(`${k} unconfigured WITEL location`);
    return types_1.Witel[v.toUpperCase()];
});
exports.BOT_DATEL = env('bot_datel', (v, k) => {
    if (!v)
        throw new TypeError(`${k} unconfigured DATEL location`);
    return v;
});

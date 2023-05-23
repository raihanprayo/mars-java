"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.isDefined = exports.isNull = exports.isUndef = void 0;
function isUndef(o) {
    return typeof o === 'undefined';
}
exports.isUndef = isUndef;
function isNull(o) {
    return {}.toString.call(o) === '[object Null]';
}
exports.isNull = isNull;
function isDefined(o) {
    return !isNull(o) && !isUndef(o);
}
exports.isDefined = isDefined;

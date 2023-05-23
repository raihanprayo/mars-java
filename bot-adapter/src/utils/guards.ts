export function isUndef(o: any): o is undefined | void {
    return typeof o === 'undefined';
}

export function isNull(o: any): o is null {
    return {}.toString.call(o) === '[object Null]';
}

export function isDefined<T>(o: T): o is Exclude<T, null | undefined | void> {
    return !isNull(o) && !isUndef(o)
}

export function isString(o: any): o is string {
    return typeof o === "string" || o instanceof String;
}
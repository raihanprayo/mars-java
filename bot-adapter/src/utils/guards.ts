function guardFactory<T>(
    fn: (input: any) => boolean
): GuardFn<T> {

    function k(o: any): o is T {
        return fn(o);
    }

    function n<I>(o: I): o is Exclude<I, T> {
        return !fn(o);
    }

    k.non = n;
    return k;
}

export interface GuardFn<T> {
    (o: any): o is T;

    non<I>(o: I): o is Exclude<I, T>;
}

export const isArr = guardFactory<Array<any>>(function (o) {
    return Array.isArray(o);
});
/** Check is valid `string`, either `primitives` or `Object` */
export const isStr = guardFactory<string>((o) => typeof o === "string" || o instanceof String);
/** Check is valid `number`, either `primitives` or `Object` */
export const isNum = guardFactory<number>((o) => typeof o === "number" || o instanceof Number);
/** Check is valid `boolean`, either `primitives` or `Object` */
export const isBool = guardFactory<boolean>((o) => typeof o === "boolean" || o instanceof Boolean);
/** Check is valid `bigint`, either `primitives` or `Object` */
export const isBigInt = guardFactory<bigint>((o) => typeof o === "bigint" || o instanceof BigInt);
/**
 * Basic check target instaceof Object,
 * also check is target `null`
 */
export const isObj = guardFactory<map>((o) => o instanceof Object);
export const isFn = guardFactory<Function>((o) => typeof o === "function");
export const isSymbol = guardFactory<symbol>((o) => typeof o === "symbol");
export const isUndef = guardFactory<undefined>((o) => typeof o === "undefined");
export const isNull = guardFactory<null>((o) => typeof o === "object" && o === null);
export const isPrimitive = guardFactory<primitives>(
    (o) => isStr(o) || isBool(o) || isNum(o) || isBigInt(o)
);
export const isFalsy = guardFactory<Falsy>((o) => {
    if (isBoxedPrimitive(o)) return "" == o || 0 == o || -0 == o || 0n == o || false == o;
    return !!o === false;
});

export function isDefined<T>(o: T): o is Exclude<T, Empty> {
    return !isUndef(o) && !isNull(o);
}

export function isTruthy<T>(o: T): o is Truthy<T> {
    return isFalsy.non(o);
}

// === Boxed Type ===========================================================================
export const isBoxedString = guardFactory<String>(
    (o) => typeof o === "object" && o instanceof String
);
export const isBoxedNumber = guardFactory<Number>((o) => {
    return typeof o === "object" && o instanceof Number;
});
export const isBoxedBoolean = guardFactory<Boolean>((o) => {
    return typeof o === "object" && o instanceof Boolean;
});
export const isBoxedBigInt = guardFactory<BigInt>((o) => {
    return typeof o === "object" && o instanceof Boolean;
});
export const isBoxedPrimitive = guardFactory<String | Number | Boolean | BigInt>(
    (o) => isBoxedString(o) || isBoxedNumber(o) || isBoxedBigInt(o) || isBoxedBoolean(o)
);
export const isPrimitiveCtor = guardFactory<primitivesCtor>(
    o => o === String || o === Number || o === Boolean || o === BigInt
)

// === String Aliases =======================================================================
export function isStringNumber(value: string) {
    const numRegx = /^([0-9]+)$/;
    return numRegx.test(value);
}

export function isConvertableStringNumber(value: string) {
    const numRegx = /^[1-9]([0-9]*)$/;
    return numRegx.test(value);
}

export function isConvertableToBoolean(value: any) {
    if (!isDefined(value)) return false;
    const t = value.toString().toLowerCase();
    const rgx = /^(0|1|true|false|on|off|yes|no|ya|tidak|y|n)/i;
    return rgx.test(t);
}

export function toBoolean(value: any) {
    const t = value.toString().toLowerCase();
    // const falseValue = ["0","false", "no", "n", "tidak"];
    const trueValue = ["1","true", "yes", "y", "ya", "on"];
    return trueValue.includes(t);
}
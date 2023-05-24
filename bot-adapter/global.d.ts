type map<V = any> = Record<string, V>;
type bool = boolean;
type int = number;

type primitives = string | number | boolean | bigint;
type primitivesCtor =
    | StringConstructor
    | NumberConstructor
    | BooleanConstructor
    | BigIntConstructor;

type Empty = undefined | null | void;
type Falsy = '' | -0 | 0 | 0n | false | Empty;
type Truthy<T> = Exclude<T, Falsy>;
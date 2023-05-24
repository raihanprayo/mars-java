import {
    isArr,
    isBoxedPrimitive, isConvertableToBoolean,
    isConvertableStringNumber,
    isDefined,
    isObj,
    isPrimitiveCtor,
    toBoolean
} from './guards';
import {inlineKey, objectMerge} from './object';
import {kebabCaseToCamelCase} from './string';

const storage = new WeakMap<Properties, PropertiesMetadata>();

// @ts-ignore
if (!globalThis['sc_prop_storage']) {
    Object.defineProperty(globalThis, 'sc_prop_storage', {
        value: storage,
        enumerable: false,
        configurable: false,
        writable: false,
    });
}


type FieldType = primitivesCtor | [primitivesCtor] | ((value: any) => any);
type DeepFieldType = FieldType | { [x: string]: FieldType | DeepFieldType; };

interface PropertiesMetadata {
    props: map;
    locked: bool;
    config: PropertiesConfig;
    fieldTypes: DeepFieldType;
}

interface PropertiesConfig {
    inlineCamelCaseKey?: boolean;
    inlineSeparateArray?: boolean;
}


function split_key(key: string) {
    return key
        .split('.')
        .flatMap((e) => e.split(/[\[\]]/g))
        .filter((e) => e !== '');
}

function set_r(keys: string[], value: any, store: map, types: map, properties: Properties): void {
    const [k, ...others] = keys;
    if (others.length > 0) {
        store[k!] = store[k!] ||= /^(0|([1-9][0-9]*))$/.test(others[0]!) ? [] : {};
        return set_r(
            others,
            value,
            store[k!],
            types?.[k!],
            properties
        );
    }
    const fieldType: FieldType | undefined = types?.[k!];
    store[k!] = parse(value, fieldType);
}

function get_r(keys: string[], store: map): any {
    const [k, ...others] = keys;
    try {
        if (others.length > 0) return get_r(others, store[k!]);
        if (k! in store) return store[k!];
    }
    catch (err) {
    }
}

function has_r(keys: string[], store: map): bool {
    try {
        const [k, ...others] = keys;
        if (others.length > 0) return has_r(others, store[k!]);
        return k! in store;
    }
    catch (err) {
        return false;
    }
}

function parse(value: any, type?: DeepFieldType) {
    if (isDefined(value)) {
        if (isPrimitiveCtor(type)) {
            switch (type) {
                case String:
                    return String(value);
                case Number:
                    return Number(value);
                case Boolean:
                    return toBoolean(value);
                case BigInt:
                    return BigInt(value);
            }
        }
        else if (typeof type === 'function') {
            return type(value);
        }
    }
    return value;
}

export class Properties implements Iterable<[string, any]> {
    [Symbol.iterator](): Iterator<[string, any], any, undefined> {
        return Object.entries(this.inlined)[Symbol.iterator]();
    }

    constructor(initialValue: object = {}) {
        storage.set(this, {
            locked: false,
            props: {},
            fieldTypes: {},
            config: {
                inlineCamelCaseKey: true,
                inlineSeparateArray: false,
            },
        });

        const isAcceptable =
            isObj(initialValue) &&
            !isArr(initialValue) &&
            !isBoxedPrimitive(initialValue);

        if (isAcceptable) Object.entries(initialValue).forEach((e) => this.set(...e));
    }

    get raw(): map {
        const {locked, props} = storage.get(this)!;
        if (locked) return {};
        return objectMerge({}, props);
    }

    get inlined(): map {
        const {locked, props} = storage.get(this)!;
        if (locked) return {};
        return inlineKey(props, {
            camelCaseKey: true,
            separateArray: false,
        });
    }

    get<T = any>(key: string): T {
        const t_key = kebabCaseToCamelCase(key);
        return get_r(split_key(t_key), storage.get(this)!.props);
    }

    set(key: string, value: any) {
        // if (storage.get(this)!.locked) return;
        const t_key = kebabCaseToCamelCase(key);
        // const evt = new Utils.PipeEvent(t_key, value);
        set_r(
            split_key(t_key),
            value,
            storage.get(this)!.props,
            storage.get(this)!.fieldTypes,
            this
        );
    }

    has(key: string) {
        const t_key = kebabCaseToCamelCase(key);
        return has_r(split_key(t_key), storage.get(this)!.props);
    }

    setParser(key: string, types: DeepFieldType) {
        const t_key = kebabCaseToCamelCase(key);
        set_r(
            split_key(t_key),
            types,
            storage.get(this)!.fieldTypes,
            {},
            this
        );
    }

    destroy() {
        storage.delete(this);
    }

    imports(other: Properties) {
        const inlined = other.inlined;
        for (let key in inlined)
            this.set(key, inlined[key]);
    }

}

export class StrictProperties<I extends map> extends Properties {

    constructor(initial: I) {
        super(initial);
    }

    override set<K extends keyof I>(key: K, value: I[K]) {
        super.set(key as string, value);
    }

    override get<K extends keyof I>(key: K): I[K] {
        return super.get(key as string);
    }
}
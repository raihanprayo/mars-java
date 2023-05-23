
export enum Witel {
    ROC,
    BANTEN,
    BEKASI,
    BOGOR,
    JAKBAR,
    JAKPUS,
    JAKSEL,
    JAKTIM,
    JAKUT,
    TANGERANG
};


export enum Product {
    INTERNET,
    IPTV,
    VOICE
}

export interface WorkerEvent<D = any> {
    id: EventID;
    data: D;
}

export enum EventID {
    TELEGRAM_UPDATE
}

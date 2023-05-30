import { Tg } from "./types";

export function chatSource(type: string) {
	let source: Tg.Source;
	switch (type) {
		case "group":
			source = Tg.Source.GROUP;
			break;
		case "private":
			source = Tg.Source.PRIVATE;
			break;
		case "supergroup":
			source = Tg.Source.SUPER_GROUP;
			break;
		case "channel":
			source = Tg.Source.CHANNEL;
			break;
	}
	return source!;
}

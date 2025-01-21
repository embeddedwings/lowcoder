import { language } from "i18n";

export function getDayJSLocale() {
  switch (language) {
    case "zh":
      return "zh-cn";
    case "ja":
      return "ja";
    default:
      return "en-gb";
  }
}

import { getI18nObjects, getValueByLocale, Translator } from "lowcoder-sdk";
import * as localeData from "./locales";
import { I18nObjects } from "./locales/types";

export const { trans, language } = new Translator<typeof localeData.en>(
  localeData,
  REACT_APP_LANGUAGES
);

export const i18nObjs = getI18nObjects<I18nObjects>(localeData, REACT_APP_LANGUAGES);

export function getEchartsLocale() {
  return getValueByLocale("EN", (locale) => {
    switch (locale.language) {
      case "en":
        return "EN";
      case "ja":
        return "JA";
    }
  });
}

export function getCalendarLocale() {
  switch (language) {
    case "zh":
      return "zh-cn";
    case "pt":
      return "pt-br";
    case "ja":
      return "ja";
    default:
      return "en-gb";
  }
}

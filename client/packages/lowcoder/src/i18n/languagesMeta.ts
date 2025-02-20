import {
    // Flag_de, 
    Flag_gb, 
    // Flag_it, 
    // Flag_cn,
    // Flag_fr,
    // Flag_ru,
    // Flag_cz,
    // Flag_pl,
    // Flag_es,
    // Flag_vi,
    // Flag_id,
    // Flag_tr,
    // Flag_th,
    // Flag_ar,
    // Flag_pt,
    // Flag_br,
    Flag_jp
  } from "lowcoder-design";
// import { es, ru } from "./locales";

// Define the structure for a single language's metadata
export interface LanguageMeta {
    languageName: string;
    flag: React.FC<React.SVGProps<SVGSVGElement>>;
  }
  
  // Define the structure for the container of all language metadatas
  export interface LanguagesMetadata {
    [key: string]: LanguageMeta;
  }
  
  // Example metadata object
  export const languagesMetadata: LanguagesMetadata = {
    en: {
      languageName: "English",
      flag: Flag_gb
    },
    // zh: {
    //   languageName: "中文",
    //   flag: Flag_cn
    // },
    // de: {
    //   languageName: "Deutsch",
    //   flag: Flag_de
    // },
    // pt: {
    //     languageName: "Português",
    //     flag: Flag_br
    // },
    // it: {
    //   languageName: "Italiano",
    //   flag: Flag_it
    // },
    // es: {
    //   languageName: "Español",
    //   flag: Flag_es
    // },
    // ru: {
    //   languageName: "Русский",
    //   flag: Flag_ru
    // },
    ja: {
      languageName: "日本語",
      flag: Flag_jp
    },
  };
  
import { I18nObjects } from "./types";

export const enObj: I18nObjects = {
  jsonForm: {
    defaultSchema: {
      title: "ユーザー情報",
      description: "フォーム例",
      type: "object",
      required: ["name", "phone"],
      properties: {
        name: {
          type: "string",
          title: "名前",
        },
        phone: {
          type: "string",
          title: "電話番号",
          minLength: 11,
        },
        birthday: {
          type: "string",
          title: "誕生日",
        },
      },
    },
    defaultUiSchema: {
      name: {
        "ui:autofocus": true,
        "ui:emptyValue": "",
      },
      phone: {
        "ui:help": "少なくとも11文字",
      },
      birthday: {
        "ui:widget": "date",
      },
    },
    defaultFormData: {
      name: "David",
      phone: "13488886666",
      birthday: "1980-03-16",
    },
  },
  table: {
    columns: [
      { key: "id", title: "ID" },
      { key: "name", title: "名前" },
      { key: "date", title: "日付" },
      { key: "department", title: "部署", isTag: true },
    ],
    defaultData: [
      {
        id: 1,
        name: "Reagen Gilberthorpe",
        date: "2022/7/5",
        department: "マーケティング",
      },
      {
        id: 2,
        name: "Haroun Lortzing",
        date: "2022/11/6",
        department: "人事",
      },
      {
        id: 3,
        name: "Garret Kilmaster",
        date: "2021/11/14",
        department: "研究開発",
      },
      {
        id: 4,
        name: "Israel Harrowsmith",
        date: "2022/4/3",
        department: "トレーニング",
      },
      {
        id: 5,
        name: "Loren O'Lagen",
        date: "2022/9/10",
        department: "サービス",
      },
      {
        id: 6,
        name: "Wallis Hothersall",
        date: "2022/4/18",
        department: "会計",
      },
      {
        id: 7,
        name: "Kaia Biskup",
        date: "2022/3/4",
        department: "営業",
      },
      {
        id: 8,
        name: "Travers Saterweyte",
        date: "2022/1/9",
        department: "人事",
      },
      {
        id: 9,
        name: "Mikey Niemetz",
        date: "2022/1/4",
        department: "マーケティング",
      },
      {
        id: 10,
        name: "Mano Meckiff",
        date: "2022/2/19",
        department: "研究開発",
      },
    ],
  },
  editorTutorials: {
    mockDataUrl: "https://6523073ef43b179384152c4f.mockapi.io/api/lowcoder/users",
    data: (code) => (
      <>
        現在のコンポーネントの状態とすべての設定およびデータがここに一覧表示されます。ハンドルバー式を使ってこのデータを参照できます。
        例: {code("{{table1.selectedRow}}")}.
      </>
    ),
    compProperties: (code) => (
      <>
        コンポーネントを選択すると、そのプロパティが右側に表示されます。ここでデータバインディングを設定できます。すべての静的データを削除し、次のハンドルバー式を入力してください:
        {code("{{query1.data}}")}. これにより、クエリのデータがテーブルにバインドされます。クエリがデータを更新すると、テーブルも自動的にデータを更新します。
      </>
    ),
  },
  cascader: [
    {
      value: "カリフォルニア",
      label: "カリフォルニア",
      children: [
        {
          value: "サンフランシスコ",
          label: "サンフランシスコ",
          children: [
            {
              value: "ゴールデンゲートブリッジ",
              label: "ゴールデンゲートブリッジ",
            },
          ],
        },
      ],
    },
    {
      value: "ニューサウスウェールズ",
      label: "ニューサウスウェールズ",
      children: [
        {
          value: "シドニー",
          label: "シドニー",
          children: [
            {
              value: "シドニーオペラハウス",
              label: "シドニーオペラハウス",
            },
          ],
        },
      ],
    },
  ],
  cascaderDefult: ["カリフォルニア", "サンフランシスコ", "ゴールデンゲートブリッジ"],
};

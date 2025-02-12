const ProductDescriptions: ProductDescription = {

  // Support & Ticket System Subscription
  SupportProduct : {
    "en" : '',
  },

  // Media Package Subscription
  MediaPackageProduct: {
    "en": '',
  }
};

export type Translations = {
  [key: string]: string; // Each language key maps to a string
};

export type ProductDescription = {
  [productId: string]: Translations; // Each product ID maps to its translations
};

export default ProductDescriptions;

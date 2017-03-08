var algoliaComponents = require('algolia-components');
var fs = require('fs');

import {rootPath} from './path';

const prod = process.env.NODE_ENV === 'production';
const docsDist = process.env.DOCS_DIST;

var content = JSON.parse(fs.readFileSync('./src/data/communityHeader.json', 'utf8').toString());
var header = algoliaComponents.communityHeader(content);

export default {
  docsDist:  docsDist? docsDist :
             prod ? rootPath('docs') : // avoids publishing an `npm start`ed build if running.
             rootPath('docs-preview'),
  publicPath: prod ? '/<i></i>nstantsearch-android/' :
    '/',
  header: header
};
